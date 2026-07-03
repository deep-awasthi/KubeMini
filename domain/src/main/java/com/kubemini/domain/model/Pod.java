package com.kubemini.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class Pod {
  private final String id;
  private final String deploymentId;
  private final String namespace;
  private final String name;
  private final ResourceRequirements resources;
  private final Map<String, String> labels;
  private final Instant createdAt;
  private String image;
  private String nodeId;
  private String containerId;
  private PodPhase phase;
  private int restartCount;

  public Pod(
      String id,
      String deploymentId,
      String namespace,
      String name,
      String image,
      ResourceRequirements resources,
      Map<String, String> labels,
      String nodeId,
      String containerId,
      PodPhase phase,
      int restartCount,
      Instant createdAt) {
    this.id = Objects.requireNonNull(id);
    this.deploymentId = Objects.requireNonNull(deploymentId);
    this.namespace = namespace == null || namespace.isBlank() ? "default" : namespace;
    this.name = Objects.requireNonNull(name);
    this.image = Objects.requireNonNull(image);
    this.resources = Objects.requireNonNull(resources);
    this.labels = Map.copyOf(labels == null ? Map.of() : labels);
    this.nodeId = nodeId;
    this.containerId = containerId;
    this.phase = Objects.requireNonNull(phase);
    this.restartCount = restartCount;
    this.createdAt = Objects.requireNonNull(createdAt);
  }

  public void assign(String nextNodeId) {
    nodeId = nextNodeId;
    phase = PodPhase.PENDING;
  }

  public void markRunning(String nextContainerId) {
    containerId = nextContainerId;
    phase = PodPhase.RUNNING;
  }

  public void markFailed() {
    phase = PodPhase.FAILED;
  }

  public void restart() {
    restartCount++;
    phase = PodPhase.RESTARTING;
  }

  public void terminate() {
    phase = PodPhase.TERMINATING;
  }

  public String id() {
    return id;
  }

  public String deploymentId() {
    return deploymentId;
  }

  public String namespace() {
    return namespace;
  }

  public String name() {
    return name;
  }

  public String image() {
    return image;
  }

  public ResourceRequirements resources() {
    return resources;
  }

  public Map<String, String> labels() {
    return labels;
  }

  public String nodeId() {
    return nodeId;
  }

  public String containerId() {
    return containerId;
  }

  public PodPhase phase() {
    return phase;
  }

  public int restartCount() {
    return restartCount;
  }

  public Instant createdAt() {
    return createdAt;
  }
}
