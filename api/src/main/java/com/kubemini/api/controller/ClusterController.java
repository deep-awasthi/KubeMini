package com.kubemini.api.controller;

import com.kubemini.application.port.MetricsCollector;
import com.kubemini.application.service.ClusterStateService;
import com.kubemini.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cluster")
public class ClusterController {
  private final ClusterStateService clusterState;
  private final MetricsCollector metricsCollector;

  public ClusterController(ClusterStateService clusterState, MetricsCollector metricsCollector) {
    this.clusterState = clusterState;
    this.metricsCollector = metricsCollector;
  }

  @GetMapping
  public ApiResponse<?> cluster() {
    return ApiResponse.ok(clusterState.snapshot());
  }

  @GetMapping("/status")
  public ApiResponse<?> status() {
    var metrics = clusterState.metrics();
    metricsCollector.record(metrics);
    return ApiResponse.ok(metrics);
  }
}
