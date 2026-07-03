package com.kubemini.api.dto;

import com.kubemini.domain.model.Deployment;
import com.kubemini.domain.model.KubeService;
import com.kubemini.domain.model.Node;
import com.kubemini.domain.model.Pod;
import java.time.Instant;
import java.util.Map;

public final class DtoMapper {
  private DtoMapper() {}

  public static NodeResponse node(Node node) {
    return new NodeResponse(
        node.id(),
        node.name(),
        node.host(),
        node.status().name(),
        resources(node.capacity()),
        resources(node.allocated()),
        node.labels(),
        node.lastHeartbeatAt());
  }

  public static DeploymentResponse deployment(Deployment deployment, long availableReplicas) {
    return new DeploymentResponse(
        deployment.id(),
        deployment.namespace(),
        deployment.name(),
        deployment.image(),
        deployment.status().name(),
        deployment.desiredReplicas(),
        availableReplicas,
        deployment.schedulerAlgorithm().name(),
        deployment.generation(),
        deployment.history(),
        deployment.createdAt());
  }

  public static PodResponse pod(Pod pod) {
    return new PodResponse(
        pod.id(),
        pod.deploymentId(),
        pod.namespace(),
        pod.name(),
        pod.image(),
        pod.nodeId(),
        pod.containerId(),
        pod.phase().name(),
        pod.restartCount(),
        pod.createdAt());
  }

  public static ServiceResponse service(KubeService service) {
    return new ServiceResponse(
        service.id(),
        service.namespace(),
        service.name(),
        service.type().name(),
        service.virtualIp(),
        service.port(),
        service.targetPort(),
        service.selector());
  }

  private static ResourceResponse resources(com.kubemini.domain.model.ResourceQuantity quantity) {
    return new ResourceResponse(quantity.cpuMilli(), quantity.memoryMi(), quantity.storageMi());
  }

  public record ResourceResponse(int cpuMilli, long memoryMi, long diskMi) {}

  public record NodeResponse(
      String id,
      String name,
      String host,
      String status,
      ResourceResponse capacity,
      ResourceResponse allocated,
      Map<String, String> labels,
      Instant lastHeartbeatAt) {}

  public record DeploymentResponse(
      String id,
      String namespace,
      String name,
      String image,
      String status,
      int replicas,
      long availableReplicas,
      String scheduler,
      int generation,
      java.util.List<String> history,
      Instant createdAt) {}

  public record PodResponse(
      String id,
      String deploymentId,
      String namespace,
      String name,
      String image,
      String nodeId,
      String containerId,
      String phase,
      int restartCount,
      Instant createdAt) {}

  public record ServiceResponse(
      String id,
      String namespace,
      String name,
      String type,
      String virtualIp,
      Integer port,
      Integer targetPort,
      Map<String, String> selector) {}
}
