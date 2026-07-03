package com.kubemini.application.scheduler;

import com.kubemini.domain.model.Node;
import com.kubemini.domain.model.Pod;
import java.util.List;
import java.util.Optional;

public interface Scheduler {
  Optional<Node> schedule(Pod pod, List<Node> nodes);
}
