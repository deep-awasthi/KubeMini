package com.kubemini.infrastructure.persistence;

import com.kubemini.application.port.PodRepository;
import com.kubemini.domain.model.Pod;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPodRepositoryAdapter implements PodRepository {
  private final SpringDataPodRepository repository;

  public JpaPodRepositoryAdapter(SpringDataPodRepository repository) {
    this.repository = repository;
  }

  @Override
  public Pod save(Pod pod) {
    return DomainMapper.toDomain(repository.save(DomainMapper.toEntity(pod)));
  }

  @Override
  public Optional<Pod> findById(String id) {
    return repository.findById(id).map(DomainMapper::toDomain);
  }

  @Override
  public List<Pod> findAll() {
    return repository.findAll().stream().map(DomainMapper::toDomain).toList();
  }

  @Override
  public List<Pod> findByDeploymentId(String deploymentId) {
    return repository.findByDeploymentId(deploymentId).stream().map(DomainMapper::toDomain).toList();
  }

  @Override
  public List<Pod> findByNodeId(String nodeId) {
    return repository.findByNodeId(nodeId).stream().map(DomainMapper::toDomain).toList();
  }

  @Override
  public void deleteById(String id) {
    repository.deleteById(id);
  }
}
