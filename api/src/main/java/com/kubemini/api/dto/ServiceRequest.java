package com.kubemini.api.dto;

import com.kubemini.application.in.CreateServiceCommand;
import com.kubemini.domain.model.ServiceType;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record ServiceRequest(
    String namespace,
    @NotBlank String name,
    ServiceType type,
    Integer port,
    Integer targetPort,
    Map<String, String> selector) {
  public CreateServiceCommand toCommand() {
    return new CreateServiceCommand(namespace, name, type, port, targetPort, selector);
  }
}
