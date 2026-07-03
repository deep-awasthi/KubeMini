package com.kubemini.application.in;

import com.kubemini.domain.model.ServiceType;
import java.util.Map;

public record CreateServiceCommand(
    String namespace,
    String name,
    ServiceType type,
    Integer port,
    Integer targetPort,
    Map<String, String> selector) {}
