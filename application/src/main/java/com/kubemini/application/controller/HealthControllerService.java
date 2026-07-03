package com.kubemini.application.controller;

import com.kubemini.application.port.ContainerRuntime;
import com.kubemini.application.port.EventPublisher;
import com.kubemini.application.port.PodRepository;
import com.kubemini.common.event.ClusterEvent;
import com.kubemini.domain.model.PodPhase;
import java.util.Map;

public class HealthControllerService {
  private final PodRepository pods;
  private final ContainerRuntime runtime;
  private final EventPublisher events;

  public HealthControllerService(PodRepository pods, ContainerRuntime runtime, EventPublisher events) {
    this.pods = pods;
    this.runtime = runtime;
    this.events = events;
  }

  public void restartUnhealthyPods() {
    pods.findAll().stream()
        .filter(pod -> pod.phase() == PodPhase.FAILED || pod.phase() == PodPhase.CRASH_LOOP_BACK_OFF)
        .forEach(
            pod -> {
              pod.restart();
              runtime.restart(pod);
              pods.save(pod);
              events.publish(ClusterEvent.of("HealthCheckFailed", pod.id(), "Unhealthy pod restarted", Map.of()));
            });
  }
}
