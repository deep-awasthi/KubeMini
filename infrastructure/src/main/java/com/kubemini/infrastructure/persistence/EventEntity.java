package com.kubemini.infrastructure.persistence;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "cluster_events")
class EventEntity {
  @Id String id;
  String typeName;
  String aggregateId;
  String message;

  @ElementCollection(fetch = FetchType.EAGER)
  Map<String, String> attributes = new HashMap<>();

  Instant occurredAt;
}
