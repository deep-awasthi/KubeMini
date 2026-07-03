package com.kubemini.infrastructure.persistence;

import com.kubemini.application.port.DeploymentRepository;
import com.kubemini.domain.model.Deployment;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDeploymentRepositoryAdapter implements DeploymentRepository {
  private final SpringDataDeploymentRepository repository;

  public JpaDeploymentRepositoryAdapter(SpringDataDeploymentRepository repository) {
    this.repository = repository;
  }

  @Override
  public Deployment save(Deployment deployment) {
    return DomainMapper.toDomain(repository.save(DomainMapper.toEntity(deployment)));
  }

  @Override
  public Optional<Deployment> findById(String id) {
    return repository.findById(id).map(DomainMapper::toDomain);
  }

  @Override
  public List<Deployment> findAll() {
    return repository.findAll().stream().map(DomainMapper::toDomain).toList();
  }

  @Override
  public void deleteById(String id) {
    repository.deleteById(id);
  }
}
