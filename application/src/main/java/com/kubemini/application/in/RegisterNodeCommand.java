package com.kubemini.application.in;

import java.util.Map;

public record RegisterNodeCommand(
    String name, String host, String cpuCapacity, String memoryCapacity, String diskCapacity, Map<String, String> labels) {}
