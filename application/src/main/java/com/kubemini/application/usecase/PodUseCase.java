package com.kubemini.application.usecase;

import com.kubemini.application.port.ContainerRuntime;
import com.kubemini.application.port.EventPublisher;
import com.kubemini.application.port.PodRepository;
import com.kubemini.common.error.ErrorCode;
import com.kubemini.common.error.KubeMiniException;
import com.kubemini.common.event.ClusterEvent;
import com.kubemini.domain.model.Pod;
import java.util.List;
import java.util.Map;

public class PodUseCase {
  private final PodRepository pods;
  private final ContainerRuntime runtime;
  private final EventPublisher events;

  public PodUseCase(PodRepository pods, ContainerRuntime runtime, EventPublisher events) {
    this.pods = pods;
    this.runtime = runtime;
    this.events = events;
  }

  public List<Pod> list() {
    return pods.findAll();
  }

  public Pod get(String id) {
    return pods.findById(id).orElseThrow(() -> new KubeMiniException(ErrorCode.POD_NOT_FOUND, "Pod not found: " + id));
  }

  public void delete(String id) {
    var pod = get(id);
    runtime.stop(pod);
    pods.deleteById(id);
    events.publish(ClusterEvent.of("PodDeleted", id, "Pod deleted", Map.of()));
  }

  public Pod restart(String id) {
    var pod = get(id);
    pod.restart();
    runtime.restart(pod);
    pod.markRunning(pod.containerId());
    var saved = pods.save(pod);
    events.publish(ClusterEvent.of("ContainerRestarted", id, "Container restarted", Map.of()));
    return saved;
  }
}
