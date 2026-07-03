package com.kubemini.application.port;

import com.kubemini.common.event.ClusterEvent;

public interface EventPublisher {
  void publish(ClusterEvent event);
}
