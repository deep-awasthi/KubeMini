package com.kubemini.api.websocket;

import com.kubemini.infrastructure.event.PersistedClusterEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClusterEventWebSocketPublisher {
  private final SimpMessagingTemplate messaging;

  public ClusterEventWebSocketPublisher(SimpMessagingTemplate messaging) {
    this.messaging = messaging;
  }

  @EventListener
  public void onEvent(PersistedClusterEvent event) {
    messaging.convertAndSend("/topic/cluster-events", event.event());
  }
}
