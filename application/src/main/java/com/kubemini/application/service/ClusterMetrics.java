package com.kubemini.application.service;

public record ClusterMetrics(
    int nodes,
    int readyNodes,
    int deployments,
    int runningPods,
    int pendingPods,
    int failedPods,
    int restartCount,
    int cpuAllocatedMilli,
    long memoryAllocatedMi,
    long diskAllocatedMi) {}
