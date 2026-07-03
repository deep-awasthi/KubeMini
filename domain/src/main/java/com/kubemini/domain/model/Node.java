package com.kubemini.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class Node {
  private final String id;
  private final String name;
  private final String host;
  private final ResourceQuantity capacity;
  private final Map<String, String> labels;
  private NodeStatus status;
  private ResourceQuantity allocated;
  private Instant lastHeartbeatAt;

  public Node(
      String id,
      String name,
      String host,
      ResourceQuantity capacity,
      Map<String, String> labels,
      NodeStatus status,
      ResourceQuantity allocated,
      Instant lastHeartbeatAt) {
    this.id = Objects.requireNonNull(id);
    this.name = Objects.requireNonNull(name);
    this.host = Objects.requireNonNull(host);
    this.capacity = Objects.requireNonNull(capacity);
    this.labels = Map.copyOf(labels == null ? Map.of() : labels);
    this.status = Objects.requireNonNull(status);
    this.allocated = Objects.requireNonNull(allocated);
    this.lastHeartbeatAt = Objects.requireNonNull(lastHeartbeatAt);
  }

  public boolean canSchedule(ResourceQuantity request) {
    return status == NodeStatus.READY && allocated.plus(request).fits(capacity);
  }

  public void allocate(ResourceQuantity request) {
    allocated = allocated.plus(request);
  }

  public void release(ResourceQuantity request) {
    allocated = allocated.minus(request);
  }

  public void heartbeat() {
    status = NodeStatus.READY;
    lastHeartbeatAt = Instant.now();
  }

  public void drain() {
    status = NodeStatus.DRAINING;
  }

  public void cordon() {
    status = NodeStatus.CORDONED;
  }

  public void markOffline() {
    status = NodeStatus.OFFLINE;
  }

  public ResourceQuantity available() {
    return capacity.minus(allocated);
  }

  public String id() {
    return id;
  }

  public String name() {
    return name;
  }

  public String host() {
    return host;
  }

  public ResourceQuantity capacity() {
    return capacity;
  }

  public Map<String, String> labels() {
    return labels;
  }

  public NodeStatus status() {
    return status;
  }

  public ResourceQuantity allocated() {
    return allocated;
  }

  public Instant lastHeartbeatAt() {
    return lastHeartbeatAt;
  }
}
