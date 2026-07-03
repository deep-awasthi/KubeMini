package com.kubemini.application.port;

import com.kubemini.domain.model.Pod;
import java.util.List;
import java.util.Optional;

public interface PodRepository {
  Pod save(Pod pod);

  Optional<Pod> findById(String id);

  List<Pod> findAll();

  List<Pod> findByDeploymentId(String deploymentId);

  List<Pod> findByNodeId(String nodeId);

  void deleteById(String id);
}
