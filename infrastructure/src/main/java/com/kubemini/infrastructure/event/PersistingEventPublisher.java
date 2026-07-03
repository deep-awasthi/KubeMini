package com.kubemini.infrastructure.event;

import com.kubemini.application.port.EventPublisher;
import com.kubemini.application.port.EventStore;
import com.kubemini.common.event.ClusterEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PersistingEventPublisher implements EventPublisher {
  private final EventStore store;
  private final ApplicationEventPublisher publisher;

  public PersistingEventPublisher(EventStore store, ApplicationEventPublisher publisher) {
    this.store = store;
    this.publisher = publisher;
  }

  @Override
  public void publish(ClusterEvent event) {
    var persisted = store.append(event);
    publisher.publishEvent(new PersistedClusterEvent(persisted));
  }
}
