package com.kubemini.application.usecase;

import com.kubemini.application.in.CreateDeploymentCommand;
import com.kubemini.application.port.DeploymentRepository;
import com.kubemini.application.port.EventPublisher;
import com.kubemini.application.port.PodRepository;
import com.kubemini.common.error.ErrorCode;
import com.kubemini.common.error.KubeMiniException;
import com.kubemini.common.event.ClusterEvent;
import com.kubemini.common.util.Ids;
import com.kubemini.domain.model.Deployment;
import com.kubemini.domain.model.DeploymentStatus;
import com.kubemini.domain.model.ResourceQuantity;
import com.kubemini.domain.model.ResourceRequirements;
import com.kubemini.domain.model.SchedulerAlgorithm;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class DeploymentUseCase {
  private final DeploymentRepository deployments;
  private final PodRepository pods;
  private final DeploymentReconciler reconciler;
  private final EventPublisher events;

  public DeploymentUseCase(
      DeploymentRepository deployments,
      PodRepository pods,
      DeploymentReconciler reconciler,
      EventPublisher events) {
    this.deployments = deployments;
    this.pods = pods;
    this.reconciler = reconciler;
    this.events = events;
  }

  public Deployment create(CreateDeploymentCommand command) {
    validateReplicas(command.replicas());
    var deployment =
        new Deployment(
            Ids.prefixed("dep"),
            command.namespace(),
            command.name(),
            command.image(),
            command.replicas(),
            DeploymentStatus.PENDING,
            resources(command),
            command.scheduler() == null ? SchedulerAlgorithm.RESOURCE_AWARE : command.scheduler(),
            command.labels(),
            Instant.now(),
            false,
            1,
            List.of(command.image()));
    var saved = deployments.save(deployment);
    events.publish(ClusterEvent.of("DeploymentCreated", saved.id(), "Deployment created", Map.of("name", saved.name())));
    reconciler.reconcile(saved.id());
    return get(saved.id());
  }

  public List<Deployment> list() {
    return deployments.findAll();
  }

  public Deployment get(String id) {
    return deployments.findById(id).orElseThrow(() -> new KubeMiniException(ErrorCode.DEPLOYMENT_NOT_FOUND, "Deployment not found: " + id));
  }

  public Deployment update(String id, CreateDeploymentCommand command) {
    var deployment = get(id);
    deployment.updateImage(command.image());
    deployment.scale(command.replicas());
    var saved = deployments.save(deployment);
    events.publish(ClusterEvent.of("RollingUpdateStarted", id, "Deployment update requested", Map.of("image", saved.image())));
    reconciler.reconcile(id);
    return get(id);
  }

  public void delete(String id) {
    var deployment = get(id);
    for (var pod : pods.findByDeploymentId(id)) {
      pods.deleteById(pod.id());
    }
    deployment.markDeleted();
    deployments.deleteById(id);
    events.publish(ClusterEvent.of("DeploymentDeleted", id, "Deployment deleted", Map.of()));
  }

  public Deployment scale(String id, int replicas) {
    validateReplicas(replicas);
    var deployment = get(id);
    deployment.scale(replicas);
    deployments.save(deployment);
    events.publish(ClusterEvent.of("DeploymentScaled", id, "Deployment scaled", Map.of("replicas", Integer.toString(replicas))));
    reconciler.reconcile(id);
    return get(id);
  }

  public Deployment pause(String id) {
    var deployment = get(id);
    deployment.pause();
    return deployments.save(deployment);
  }

  public Deployment resume(String id) {
    var deployment = get(id);
    deployment.resume();
    deployments.save(deployment);
    reconciler.reconcile(id);
    return get(id);
  }

  public Deployment rollback(String id) {
    var deployment = get(id);
    deployment.rollback();
    deployments.save(deployment);
    events.publish(ClusterEvent.of("DeploymentRolledBack", id, "Deployment rolled back", Map.of("image", deployment.image())));
    reconciler.reconcile(id);
    return get(id);
  }

  private static ResourceRequirements resources(CreateDeploymentCommand command) {
    return new ResourceRequirements(
        ResourceQuantity.of(command.cpuRequest(), command.memoryRequest(), command.storageRequest()),
        ResourceQuantity.of(command.cpuLimit(), command.memoryLimit(), command.storageLimit()));
  }

  private static void validateReplicas(int replicas) {
    if (replicas < 0) {
      throw new KubeMiniException(ErrorCode.VALIDATION_FAILED, "Replicas must be zero or greater.");
    }
  }
}
