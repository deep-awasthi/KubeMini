package com.kubemini.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataNodeRepository extends JpaRepository<NodeEntity, String> {}

interface SpringDataDeploymentRepository extends JpaRepository<DeploymentEntity, String> {}

interface SpringDataPodRepository extends JpaRepository<PodEntity, String> {
  List<PodEntity> findByDeploymentId(String deploymentId);

  List<PodEntity> findByNodeId(String nodeId);
}

interface SpringDataServiceRepository extends JpaRepository<ServiceEntity, String> {
  Optional<ServiceEntity> findByNamespaceNameAndName(String namespaceName, String name);
}

interface SpringDataEventRepository extends JpaRepository<EventEntity, String> {
  List<EventEntity> findTop100ByOrderByOccurredAtDesc();
}
