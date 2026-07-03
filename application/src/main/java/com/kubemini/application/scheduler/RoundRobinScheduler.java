package com.kubemini.application.scheduler;

import com.kubemini.domain.model.Node;
import com.kubemini.domain.model.Pod;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

final class RoundRobinScheduler implements Scheduler {
  private final AtomicInteger cursor = new AtomicInteger();

  @Override
  public Optional<Node> schedule(Pod pod, List<Node> nodes) {
    var candidates = nodes.stream().filter(n -> n.canSchedule(pod.resources().requests())).toList();
    if (candidates.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(candidates.get(Math.floorMod(cursor.getAndIncrement(), candidates.size())));
  }
}
