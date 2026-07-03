package com.kubemini.infrastructure.docker;

import com.kubemini.application.port.ContainerRuntime;
import com.kubemini.domain.model.Pod;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "kubemini.runtime.mode", havingValue = "simulated", matchIfMissing = true)
public class SimulatedContainerRuntime implements ContainerRuntime {
  @Override
  public String start(Pod pod) {
    return "sim-" + pod.id();
  }

  @Override
  public void stop(Pod pod) {}

  @Override
  public void restart(Pod pod) {}

  @Override
  public String logs(Pod pod) {
    return "Simulated logs for pod " + pod.id();
  }
}
