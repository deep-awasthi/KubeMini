package com.kubemini.domain.model;

import java.util.Map;

public record KubeService(
    String id,
    String namespace,
    String name,
    ServiceType type,
    String virtualIp,
    Integer port,
    Integer targetPort,
    Map<String, String> selector) {}
