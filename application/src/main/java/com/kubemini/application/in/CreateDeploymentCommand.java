package com.kubemini.application.in;

import com.kubemini.domain.model.SchedulerAlgorithm;
import java.util.Map;

public record CreateDeploymentCommand(
    String namespace,
    String name,
    String image,
    int replicas,
    String cpuRequest,
    String memoryRequest,
    String storageRequest,
    String cpuLimit,
    String memoryLimit,
    String storageLimit,
    SchedulerAlgorithm scheduler,
    Map<String, String> labels) {}
