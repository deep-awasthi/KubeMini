package com.kubemini.infrastructure.persistence;

import com.kubemini.domain.model.NodeStatus;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "nodes")
class NodeEntity {
  @Id String id;
  String name;
  String host;
  int cpuCapacityMilli;
  long memoryCapacityMi;
  long diskCapacityMi;
  int cpuAllocatedMilli;
  long memoryAllocatedMi;
  long diskAllocatedMi;

  @ElementCollection(fetch = FetchType.EAGER)
  Map<String, String> labels = new HashMap<>();

  @Enumerated(EnumType.STRING)
  NodeStatus status;

  Instant lastHeartbeatAt;
}
