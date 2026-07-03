package com.kubemini.application.scheduler;

import com.kubemini.domain.model.Node;
import com.kubemini.domain.model.Pod;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

final class RandomScheduler implements Scheduler {
  @Override
  public Optional<Node> schedule(Pod pod, List<Node> nodes) {
    var candidates = nodes.stream().filter(n -> n.canSchedule(pod.resources().requests())).toList();
    if (candidates.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(candidates.get(ThreadLocalRandom.current().nextInt(candidates.size())));
  }
}
