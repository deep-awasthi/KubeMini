package com.kubemini.api.dto;

import com.kubemini.domain.model.AutoScalingPolicy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;

public record AutoScalingRequest(
    @NotBlank String deploymentId,
    @Min(0) int minReplicas,
    @Min(1) int maxReplicas,
    @Min(1) @Max(100) int targetCpuPercent,
    @Min(1) @Max(100) int targetMemoryPercent,
    int queueLengthThreshold,
    int cooldownSeconds) {
  public AutoScalingPolicy toPolicy() {
    return new AutoScalingPolicy(
        "hpa-" + deploymentId,
        deploymentId,
        minReplicas,
        maxReplicas,
        targetCpuPercent,
        targetMemoryPercent,
        queueLengthThreshold,
        Duration.ofSeconds(cooldownSeconds));
  }
}
