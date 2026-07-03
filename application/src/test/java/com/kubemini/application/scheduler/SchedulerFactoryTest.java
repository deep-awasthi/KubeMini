package com.kubemini.application.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kubemini.domain.model.Node;
import com.kubemini.domain.model.NodeStatus;
import com.kubemini.domain.model.Pod;
import com.kubemini.domain.model.PodPhase;
import com.kubemini.domain.model.ResourceQuantity;
import com.kubemini.domain.model.ResourceRequirements;
import com.kubemini.domain.model.SchedulerAlgorithm;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SchedulerFactoryTest {
  @Test
  void leastLoadedSchedulerChoosesNodeWithLowestAllocationThatFits() {
    var low =
        new Node(
            "node-low",
            "low",
            "localhost",
            new ResourceQuantity(4000, 8192, 10240),
            Map.of(),
            NodeStatus.READY,
            new ResourceQuantity(500, 512, 0),
            Instant.now());
    var high =
        new Node(
            "node-high",
            "high",
            "localhost",
            new ResourceQuantity(4000, 8192, 10240),
            Map.of(),
            NodeStatus.READY,
            new ResourceQuantity(2500, 512, 0),
            Instant.now());
    var pod =
        new Pod(
            "pod-1",
            "dep-1",
            "default",
            "api",
            "nginx:latest",
            new ResourceRequirements(new ResourceQuantity(250, 256, 0), ResourceQuantity.ZERO),
            Map.of(),
            null,
            null,
            PodPhase.PENDING,
            0,
            Instant.now());

    var selected =
        new SchedulerFactory()
            .forAlgorithm(SchedulerAlgorithm.LEAST_LOADED)
            .schedule(pod, List.of(high, low));

    assertTrue(selected.isPresent());
    assertEquals("node-low", selected.get().id());
  }
}
