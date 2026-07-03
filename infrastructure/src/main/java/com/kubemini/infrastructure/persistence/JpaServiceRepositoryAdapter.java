package com.kubemini.infrastructure.persistence;

import com.kubemini.application.port.ServiceRepository;
import com.kubemini.domain.model.KubeService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaServiceRepositoryAdapter implements ServiceRepository {
  private final SpringDataServiceRepository repository;

  public JpaServiceRepositoryAdapter(SpringDataServiceRepository repository) {
    this.repository = repository;
  }

  @Override
  public KubeService save(KubeService service) {
    return DomainMapper.toDomain(repository.save(DomainMapper.toEntity(service)));
  }

  @Override
  public Optional<KubeService> findById(String id) {
    return repository.findById(id).map(DomainMapper::toDomain);
  }

  @Override
  public Optional<KubeService> findByName(String namespace, String name) {
    return repository.findByNamespaceNameAndName(namespace == null ? "default" : namespace, name).map(DomainMapper::toDomain);
  }

  @Override
  public List<KubeService> findAll() {
    return repository.findAll().stream().map(DomainMapper::toDomain).toList();
  }
}
