package com.kubemini.api.dto;

import com.kubemini.application.in.CreateDeploymentCommand;
import com.kubemini.domain.model.SchedulerAlgorithm;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record DeploymentRequest(
    String namespace,
    @NotBlank String name,
    @NotBlank String image,
    @Min(0) int replicas,
    String cpuRequest,
    String memoryRequest,
    String storageRequest,
    String cpuLimit,
    String memoryLimit,
    String storageLimit,
    SchedulerAlgorithm scheduler,
    Map<String, String> labels) {
  public CreateDeploymentCommand toCommand() {
    return new CreateDeploymentCommand(
        namespace,
        name,
        image,
        replicas,
        cpuRequest,
        memoryRequest,
        storageRequest,
        cpuLimit,
        memoryLimit,
        storageLimit,
        scheduler,
        labels);
  }
}
