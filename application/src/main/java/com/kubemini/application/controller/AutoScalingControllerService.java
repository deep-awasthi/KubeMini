package com.kubemini.application.controller;

import com.kubemini.application.service.ClusterMetrics;
import com.kubemini.application.usecase.DeploymentUseCase;
import com.kubemini.domain.model.AutoScalingPolicy;

public class AutoScalingControllerService {
  private final DeploymentUseCase deployments;

  public AutoScalingControllerService(DeploymentUseCase deployments) {
    this.deployments = deployments;
  }

  public void evaluate(AutoScalingPolicy policy, ClusterMetrics metrics) {
    var deployment = deployments.get(policy.deploymentId());
    if (metrics.runningPods() == 0) {
      deployments.scale(deployment.id(), Math.max(policy.minReplicas(), deployment.desiredReplicas()));
      return;
    }
    if (metrics.cpuAllocatedMilli() > policy.targetCpuPercent() * 10
        && deployment.desiredReplicas() < policy.maxReplicas()) {
      deployments.scale(deployment.id(), deployment.desiredReplicas() + 1);
    }
  }
}
