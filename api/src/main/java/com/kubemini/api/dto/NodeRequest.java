package com.kubemini.api.dto;

import com.kubemini.application.in.RegisterNodeCommand;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record NodeRequest(
    @NotBlank String name,
    @NotBlank String host,
    @NotBlank String cpuCapacity,
    @NotBlank String memoryCapacity,
    String diskCapacity,
    Map<String, String> labels) {
  public RegisterNodeCommand toCommand() {
    return new RegisterNodeCommand(name, host, cpuCapacity, memoryCapacity, diskCapacity, labels);
  }
}
