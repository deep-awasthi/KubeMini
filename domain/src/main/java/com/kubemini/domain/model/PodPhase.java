package com.kubemini.domain.model;

public enum PodPhase {
  PENDING,
  RUNNING,
  SUCCEEDED,
  FAILED,
  CRASH_LOOP_BACK_OFF,
  RESTARTING,
  TERMINATING
}
