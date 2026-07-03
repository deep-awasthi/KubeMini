package com.kubemini.application.service;

import com.kubemini.application.port.DeploymentRepository;
import com.kubemini.application.port.NodeRepository;
import com.kubemini.application.port.PodRepository;
import com.kubemini.application.port.ServiceRepository;
import com.kubemini.domain.model.ClusterSnapshot;
import com.kubemini.domain.model.NodeStatus;
import com.kubemini.domain.model.PodPhase;

public class ClusterStateService {
  private final NodeRepository nodes;
  private final DeploymentRepository deployments;
  private final PodRepository pods;
  private final ServiceRepository services;

  public ClusterStateService(
      NodeRepository nodes,
      DeploymentRepository deployments,
      PodRepository pods,
      ServiceRepository services) {
    this.nodes = nodes;
    this.deployments = deployments;
    this.pods = pods;
    this.services = services;
  }

  public ClusterSnapshot snapshot() {
    return new ClusterSnapshot(
        nodes.findAll(), deployments.findAll(), pods.findAll(), services.findAll());
  }

  public ClusterMetrics metrics() {
    var nodeList = nodes.findAll();
    var deploymentList = deployments.findAll();
    var podList = pods.findAll();
    int restartCount = podList.stream().mapToInt(p -> p.restartCount()).sum();
    var allocated =
        nodeList.stream()
            .map(n -> n.allocated())
            .reduce(com.kubemini.domain.model.ResourceQuantity.ZERO, (left, right) -> left.plus(right));
    return new ClusterMetrics(
        nodeList.size(),
        (int) nodeList.stream().filter(n -> n.status() == NodeStatus.READY).count(),
        deploymentList.size(),
        (int) podList.stream().filter(p -> p.phase() == PodPhase.RUNNING).count(),
        (int) podList.stream().filter(p -> p.phase() == PodPhase.PENDING).count(),
        (int) podList.stream().filter(p -> p.phase() == PodPhase.FAILED).count(),
        restartCount,
        allocated.cpuMilli(),
        allocated.memoryMi(),
        allocated.storageMi());
  }
}
