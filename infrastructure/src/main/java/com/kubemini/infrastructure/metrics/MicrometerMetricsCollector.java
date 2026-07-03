package com.kubemini.infrastructure.metrics;

import com.kubemini.application.port.MetricsCollector;
import com.kubemini.application.service.ClusterMetrics;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class MicrometerMetricsCollector implements MetricsCollector {
  private final AtomicInteger runningPods = new AtomicInteger();

  public MicrometerMetricsCollector(MeterRegistry registry) {
    Gauge.builder("kubemini_pods_running", runningPods, AtomicInteger::get).register(registry);
  }

  @Override
  public void record(ClusterMetrics metrics) {
    runningPods.set(metrics.runningPods());
  }
}
