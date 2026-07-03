package com.kubemini.infrastructure.persistence;

import com.kubemini.domain.model.DeploymentStatus;
import com.kubemini.domain.model.SchedulerAlgorithm;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(name = "deployments")
class DeploymentEntity {
  @Id String id;
  String namespaceName;
  String name;
  String image;
  int desiredReplicas;

  @Enumerated(EnumType.STRING)
  DeploymentStatus status;

  int cpuRequestMilli;
  long memoryRequestMi;
  long diskRequestMi;
  int cpuLimitMilli;
  long memoryLimitMi;
  long diskLimitMi;

  @Enumerated(EnumType.STRING)
  SchedulerAlgorithm schedulerAlgorithm;

  @ElementCollection(fetch = FetchType.EAGER)
  Map<String, String> labels = new HashMap<>();

  @ElementCollection(fetch = FetchType.EAGER)
  List<String> history = new ArrayList<>();

  Instant createdAt;
  boolean paused;
  int generation;
}
