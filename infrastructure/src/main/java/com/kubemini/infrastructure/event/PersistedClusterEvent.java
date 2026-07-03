package com.kubemini.infrastructure.event;

import com.kubemini.common.event.ClusterEvent;

public record PersistedClusterEvent(ClusterEvent event) {}
