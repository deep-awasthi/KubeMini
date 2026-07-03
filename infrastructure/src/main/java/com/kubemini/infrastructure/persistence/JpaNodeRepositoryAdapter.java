package com.kubemini.infrastructure.persistence;

import com.kubemini.application.port.NodeRepository;
import com.kubemini.domain.model.Node;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaNodeRepositoryAdapter implements NodeRepository {
  private final SpringDataNodeRepository repository;

  public JpaNodeRepositoryAdapter(SpringDataNodeRepository repository) {
    this.repository = repository;
  }

  @Override
  public Node save(Node node) {
    return DomainMapper.toDomain(repository.save(DomainMapper.toEntity(node)));
  }

  @Override
  public Optional<Node> findById(String id) {
    return repository.findById(id).map(DomainMapper::toDomain);
  }

  @Override
  public List<Node> findAll() {
    return repository.findAll().stream().map(DomainMapper::toDomain).toList();
  }

  @Override
  public void deleteById(String id) {
    repository.deleteById(id);
  }
}
