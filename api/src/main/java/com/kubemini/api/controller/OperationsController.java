package com.kubemini.api.controller;

import com.kubemini.api.dto.AutoScalingRequest;
import com.kubemini.application.controller.AutoScalingControllerService;
import com.kubemini.application.controller.HealthControllerService;
import com.kubemini.application.port.EventStore;
import com.kubemini.application.service.ClusterStateService;
import com.kubemini.application.usecase.LogUseCase;
import com.kubemini.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OperationsController {
  private final AutoScalingControllerService autoScaling;
  private final HealthControllerService healthController;
  private final ClusterStateService clusterState;
  private final LogUseCase logs;
  private final EventStore events;

  public OperationsController(
      AutoScalingControllerService autoScaling,
      HealthControllerService healthController,
      ClusterStateService clusterState,
      LogUseCase logs,
      EventStore events) {
    this.autoScaling = autoScaling;
    this.healthController = healthController;
    this.clusterState = clusterState;
    this.logs = logs;
    this.events = events;
  }

  @PostMapping("/autoscaling")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<Void> autoscaling(@Valid @RequestBody AutoScalingRequest request) {
    autoScaling.evaluate(request.toPolicy(), clusterState.metrics());
    return ApiResponse.ok();
  }

  @GetMapping("/metrics")
  public ApiResponse<?> metrics() {
    return ApiResponse.ok(clusterState.metrics());
  }

  @GetMapping("/health")
  public ApiResponse<?> health() {
    return ApiResponse.ok(java.util.Map.of("status", "UP"));
  }

  @PostMapping("/health/reconcile")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<Void> reconcileHealth() {
    healthController.restartUnhealthyPods();
    return ApiResponse.ok();
  }

  @GetMapping("/logs/{podId}")
  public ApiResponse<?> podLogs(@PathVariable String podId) {
    return ApiResponse.ok(java.util.Map.of("podId", podId, "logs", logs.podLogs(podId)));
  }

  @GetMapping("/events")
  public ApiResponse<?> events() {
    return ApiResponse.ok(events.findLatest(100));
  }
}
