package com.kubemini.application.port;

import com.kubemini.common.event.ClusterEvent;
import java.util.List;

public interface EventStore {
  ClusterEvent append(ClusterEvent event);

  List<ClusterEvent> findLatest(int limit);
}
