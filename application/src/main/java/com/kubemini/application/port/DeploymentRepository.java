package com.kubemini.application.port;

import com.kubemini.domain.model.Deployment;
import java.util.List;
import java.util.Optional;

public interface DeploymentRepository {
  Deployment save(Deployment deployment);

  Optional<Deployment> findById(String id);

  List<Deployment> findAll();

  void deleteById(String id);
}
