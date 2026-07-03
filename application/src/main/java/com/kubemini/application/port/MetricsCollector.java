package com.kubemini.application.port;

import com.kubemini.application.service.ClusterMetrics;

public interface MetricsCollector {
  void record(ClusterMetrics metrics);
}
