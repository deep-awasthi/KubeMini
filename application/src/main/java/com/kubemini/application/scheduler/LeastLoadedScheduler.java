package com.kubemini.application.scheduler;

import com.kubemini.domain.model.Node;
import com.kubemini.domain.model.Pod;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

final class LeastLoadedScheduler implements Scheduler {
  @Override
  public Optional<Node> schedule(Pod pod, List<Node> nodes) {
    return nodes.stream()
        .filter(node -> node.canSchedule(pod.resources().requests()))
        .min(Comparator.comparingInt(node -> node.allocated().cpuMilli()));
  }
}
