package com.kubemini.domain.model;

import java.util.List;

public record ClusterSnapshot(
    List<Node> nodes,
    List<Deployment> deployments,
    List<Pod> pods,
    List<KubeService> services) {}
