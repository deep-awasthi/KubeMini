package com.kubemini.domain.model;

public enum SchedulerAlgorithm {
  LEAST_LOADED,
  ROUND_ROBIN,
  BIN_PACKING,
  RANDOM,
  RESOURCE_AWARE,
  NODE_AFFINITY,
  ANTI_AFFINITY,
  PREFERRED
}
