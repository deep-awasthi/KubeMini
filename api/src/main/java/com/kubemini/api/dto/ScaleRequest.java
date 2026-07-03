package com.kubemini.api.dto;

import jakarta.validation.constraints.Min;

public record ScaleRequest(@Min(0) int replicas) {}
