package com.kubemini.infrastructure.persistence;

import com.kubemini.common.event.ClusterEvent;
import com.kubemini.domain.model.Deployment;
import com.kubemini.domain.model.KubeService;
import com.kubemini.domain.model.Node;
import com.kubemini.domain.model.Pod;
import com.kubemini.domain.model.ResourceQuantity;
import com.kubemini.domain.model.ResourceRequirements;
import java.util.HashMap;

final class DomainMapper {
  private DomainMapper() {}

  static NodeEntity toEntity(Node node) {
    var entity = new NodeEntity();
    entity.id = node.id();
    entity.name = node.name();
    entity.host = node.host();
    entity.cpuCapacityMilli = node.capacity().cpuMilli();
    entity.memoryCapacityMi = node.capacity().memoryMi();
    entity.diskCapacityMi = node.capacity().storageMi();
    entity.cpuAllocatedMilli = node.allocated().cpuMilli();
    entity.memoryAllocatedMi = node.allocated().memoryMi();
    entity.diskAllocatedMi = node.allocated().storageMi();
    entity.labels = new HashMap<>(node.labels());
    entity.status = node.status();
    entity.lastHeartbeatAt = node.lastHeartbeatAt();
    return entity;
  }

  static Node toDomain(NodeEntity entity) {
    return new Node(
        entity.id,
        entity.name,
        entity.host,
        new ResourceQuantity(entity.cpuCapacityMilli, entity.memoryCapacityMi, entity.diskCapacityMi),
        entity.labels,
        entity.status,
        new ResourceQuantity(entity.cpuAllocatedMilli, entity.memoryAllocatedMi, entity.diskAllocatedMi),
        entity.lastHeartbeatAt);
  }

  static DeploymentEntity toEntity(Deployment deployment) {
    var entity = new DeploymentEntity();
    entity.id = deployment.id();
    entity.namespaceName = deployment.namespace();
    entity.name = deployment.name();
    entity.image = deployment.image();
    entity.desiredReplicas = deployment.desiredReplicas();
    entity.status = deployment.status();
    entity.cpuRequestMilli = deployment.resources().requests().cpuMilli();
    entity.memoryRequestMi = deployment.resources().requests().memoryMi();
    entity.diskRequestMi = deployment.resources().requests().storageMi();
    entity.cpuLimitMilli = deployment.resources().limits().cpuMilli();
    entity.memoryLimitMi = deployment.resources().limits().memoryMi();
    entity.diskLimitMi = deployment.resources().limits().storageMi();
    entity.schedulerAlgorithm = deployment.schedulerAlgorithm();
    entity.labels = new HashMap<>(deployment.labels());
    entity.history = new java.util.ArrayList<>(deployment.history());
    entity.createdAt = deployment.createdAt();
    entity.paused = deployment.paused();
    entity.generation = deployment.generation();
    return entity;
  }

  static Deployment toDomain(DeploymentEntity entity) {
    return new Deployment(
        entity.id,
        entity.namespaceName,
        entity.name,
        entity.image,
        entity.desiredReplicas,
        entity.status,
        new ResourceRequirements(
            new ResourceQuantity(entity.cpuRequestMilli, entity.memoryRequestMi, entity.diskRequestMi),
            new ResourceQuantity(entity.cpuLimitMilli, entity.memoryLimitMi, entity.diskLimitMi)),
        entity.schedulerAlgorithm,
        entity.labels,
        entity.createdAt,
        entity.paused,
        entity.generation,
        entity.history);
  }

  static PodEntity toEntity(Pod pod) {
    var entity = new PodEntity();
    entity.id = pod.id();
    entity.deploymentId = pod.deploymentId();
    entity.namespaceName = pod.namespace();
    entity.name = pod.name();
    entity.image = pod.image();
    entity.cpuRequestMilli = pod.resources().requests().cpuMilli();
    entity.memoryRequestMi = pod.resources().requests().memoryMi();
    entity.diskRequestMi = pod.resources().requests().storageMi();
    entity.cpuLimitMilli = pod.resources().limits().cpuMilli();
    entity.memoryLimitMi = pod.resources().limits().memoryMi();
    entity.diskLimitMi = pod.resources().limits().storageMi();
    entity.labels = new HashMap<>(pod.labels());
    entity.nodeId = pod.nodeId();
    entity.containerId = pod.containerId();
    entity.phase = pod.phase();
    entity.restartCount = pod.restartCount();
    entity.createdAt = pod.createdAt();
    return entity;
  }

  static Pod toDomain(PodEntity entity) {
    return new Pod(
        entity.id,
        entity.deploymentId,
        entity.namespaceName,
        entity.name,
        entity.image,
        new ResourceRequirements(
            new ResourceQuantity(entity.cpuRequestMilli, entity.memoryRequestMi, entity.diskRequestMi),
            new ResourceQuantity(entity.cpuLimitMilli, entity.memoryLimitMi, entity.diskLimitMi)),
        entity.labels,
        entity.nodeId,
        entity.containerId,
        entity.phase,
        entity.restartCount,
        entity.createdAt);
  }

  static ServiceEntity toEntity(KubeService service) {
    var entity = new ServiceEntity();
    entity.id = service.id();
    entity.namespaceName = service.namespace();
    entity.name = service.name();
    entity.type = service.type();
    entity.virtualIp = service.virtualIp();
    entity.portNumber = service.port();
    entity.targetPort = service.targetPort();
    entity.selector = new HashMap<>(service.selector() == null ? java.util.Map.of() : service.selector());
    return entity;
  }

  static KubeService toDomain(ServiceEntity entity) {
    return new KubeService(
        entity.id,
        entity.namespaceName,
        entity.name,
        entity.type,
        entity.virtualIp,
        entity.portNumber,
        entity.targetPort,
        entity.selector);
  }

  static EventEntity toEntity(ClusterEvent event) {
    var entity = new EventEntity();
    entity.id = event.id();
    entity.typeName = event.type();
    entity.aggregateId = event.aggregateId();
    entity.message = event.message();
    entity.attributes = new HashMap<>(event.attributes());
    entity.occurredAt = event.occurredAt();
    return entity;
  }

  static ClusterEvent toDomain(EventEntity entity) {
    return new ClusterEvent(entity.id, entity.typeName, entity.aggregateId, entity.message, entity.attributes, entity.occurredAt);
  }
}
