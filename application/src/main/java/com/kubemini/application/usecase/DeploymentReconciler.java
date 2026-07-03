package com.kubemini.application.usecase;

import com.kubemini.application.port.ContainerRuntime;
import com.kubemini.application.port.DeploymentRepository;
import com.kubemini.application.port.EventPublisher;
import com.kubemini.application.port.NodeRepository;
import com.kubemini.application.port.PodRepository;
import com.kubemini.application.scheduler.SchedulerFactory;
import com.kubemini.common.error.ErrorCode;
import com.kubemini.common.error.KubeMiniException;
import com.kubemini.common.event.ClusterEvent;
import com.kubemini.common.util.Ids;
import com.kubemini.domain.model.DeploymentStatus;
import com.kubemini.domain.model.Pod;
import com.kubemini.domain.model.PodPhase;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;

public class DeploymentReconciler {
  private final DeploymentRepository deployments;
  private final PodRepository pods;
  private final NodeRepository nodes;
  private final ContainerRuntime runtime;
  private final SchedulerFactory schedulerFactory;
  private final EventPublisher events;

  public DeploymentReconciler(
      DeploymentRepository deployments,
      PodRepository pods,
      NodeRepository nodes,
      ContainerRuntime runtime,
      SchedulerFactory schedulerFactory,
      EventPublisher events) {
    this.deployments = deployments;
    this.pods = pods;
    this.nodes = nodes;
    this.runtime = runtime;
    this.schedulerFactory = schedulerFactory;
    this.events = events;
  }

  public void reconcile(String deploymentId) {
    var deployment =
        deployments
            .findById(deploymentId)
            .orElseThrow(() -> new KubeMiniException(ErrorCode.DEPLOYMENT_NOT_FOUND, "Deployment not found: " + deploymentId));
    if (deployment.paused() || deployment.status() == DeploymentStatus.DELETED) {
      return;
    }

    var existing = pods.findByDeploymentId(deploymentId);
    int delta = deployment.desiredReplicas() - existing.size();
    if (delta > 0) {
      for (int i = 0; i < delta; i++) {
        createPod(deployment);
      }
    } else if (delta < 0) {
      existing.stream()
          .sorted(Comparator.comparing(Pod::createdAt).reversed())
          .limit(Math.abs(delta))
          .forEach(this::deletePod);
    }
    deployment.markRunning();
    deployments.save(deployment);
  }

  private void createPod(com.kubemini.domain.model.Deployment deployment) {
    var pod =
        new Pod(
            Ids.prefixed("pod"),
            deployment.id(),
            deployment.namespace(),
            deployment.name() + "-" + deployment.generation(),
            deployment.image(),
            deployment.resources(),
            deployment.labels(),
            null,
            null,
            PodPhase.PENDING,
            0,
            Instant.now());
    var scheduler = schedulerFactory.forAlgorithm(deployment.schedulerAlgorithm());
    var node =
        scheduler
            .schedule(pod, nodes.findAll())
            .orElseThrow(
                () ->
                    new KubeMiniException(
                        ErrorCode.INSUFFICIENT_CLUSTER_RESOURCES,
                        "Requested resources exceed available cluster capacity."));
    node.allocate(pod.resources().requests());
    nodes.save(node);
    pod.assign(node.id());
    String containerId = runtime.start(pod);
    pod.markRunning(containerId);
    pods.save(pod);
    events.publish(ClusterEvent.of("PodCreated", pod.id(), "Pod scheduled and started", Map.of("nodeId", node.id())));
  }

  private void deletePod(Pod pod) {
    pod.terminate();
    runtime.stop(pod);
    if (pod.nodeId() != null) {
      nodes.findById(pod.nodeId())
          .ifPresent(
              node -> {
                node.release(pod.resources().requests());
                nodes.save(node);
              });
    }
    pods.deleteById(pod.id());
    events.publish(ClusterEvent.of("PodDeleted", pod.id(), "Pod deleted", Map.of()));
  }
}
