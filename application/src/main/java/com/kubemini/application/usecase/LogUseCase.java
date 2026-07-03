package com.kubemini.application.usecase;

import com.kubemini.application.port.ContainerRuntime;
import com.kubemini.application.port.PodRepository;
import com.kubemini.common.error.ErrorCode;
import com.kubemini.common.error.KubeMiniException;

public class LogUseCase {
  private final PodRepository pods;
  private final ContainerRuntime runtime;

  public LogUseCase(PodRepository pods, ContainerRuntime runtime) {
    this.pods = pods;
    this.runtime = runtime;
  }

  public String podLogs(String podId) {
    var pod =
        pods.findById(podId)
            .orElseThrow(() -> new KubeMiniException(ErrorCode.POD_NOT_FOUND, "Pod not found: " + podId));
    return runtime.logs(pod);
  }
}
