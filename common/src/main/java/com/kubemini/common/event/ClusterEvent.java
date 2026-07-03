package com.kubemini.common.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ClusterEvent(
    String id,
    String type,
    String aggregateId,
    String message,
    Map<String, String> attributes,
    Instant occurredAt) {
  public static ClusterEvent of(
      String type, String aggregateId, String message, Map<String, String> attributes) {
    return new ClusterEvent(
        UUID.randomUUID().toString(), type, aggregateId, message, attributes, Instant.now());
  }
}
