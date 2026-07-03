package com.kubemini.domain.model;

import java.time.Duration;

public record AutoScalingPolicy(
    String id,
    String deploymentId,
    int minReplicas,
    int maxReplicas,
    int targetCpuPercent,
    int targetMemoryPercent,
    int queueLengthThreshold,
    Duration cooldown) {}
