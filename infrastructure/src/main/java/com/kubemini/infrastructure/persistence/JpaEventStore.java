package com.kubemini.infrastructure.persistence;

import com.kubemini.application.port.EventStore;
import com.kubemini.common.event.ClusterEvent;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaEventStore implements EventStore {
  private final SpringDataEventRepository repository;

  public JpaEventStore(SpringDataEventRepository repository) {
    this.repository = repository;
  }

  @Override
  public ClusterEvent append(ClusterEvent event) {
    return DomainMapper.toDomain(repository.save(DomainMapper.toEntity(event)));
  }

  @Override
  public List<ClusterEvent> findLatest(int limit) {
    return repository.findTop100ByOrderByOccurredAtDesc().stream().limit(limit).map(DomainMapper::toDomain).toList();
  }
}
