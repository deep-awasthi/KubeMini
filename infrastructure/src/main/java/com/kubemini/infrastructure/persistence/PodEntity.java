package com.kubemini.infrastructure.persistence;

import com.kubemini.domain.model.PodPhase;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "pods")
class PodEntity {
  @Id String id;
  String deploymentId;
  String namespaceName;
  String name;
  String image;
  int cpuRequestMilli;
  long memoryRequestMi;
  long diskRequestMi;
  int cpuLimitMilli;
  long memoryLimitMi;
  long diskLimitMi;

  @ElementCollection(fetch = FetchType.EAGER)
  Map<String, String> labels = new HashMap<>();

  String nodeId;
  String containerId;

  @Enumerated(EnumType.STRING)
  PodPhase phase;

  int restartCount;
  Instant createdAt;
}
