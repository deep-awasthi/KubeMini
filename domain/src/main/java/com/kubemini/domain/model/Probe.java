package com.kubemini.domain.model;

public record Probe(
    ProbeType type,
    String target,
    int initialDelaySeconds,
    int periodSeconds,
    int failureThreshold) {}
