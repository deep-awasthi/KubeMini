package com.kubemini.application.port;

import com.kubemini.domain.model.Pod;

public interface ContainerRuntime {
  String start(Pod pod);

  void stop(Pod pod);

  void restart(Pod pod);

  String logs(Pod pod);
}
