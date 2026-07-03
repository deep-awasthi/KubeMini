package com.kubemini.domain.model;

import com.kubemini.common.error.ErrorCode;
import com.kubemini.common.error.KubeMiniException;
import java.util.Locale;

public record ResourceQuantity(int cpuMilli, long memoryMi, long storageMi) {
  public static final ResourceQuantity ZERO = new ResourceQuantity(0, 0, 0);

  public static ResourceQuantity of(String cpu, String memory, String storage) {
    return new ResourceQuantity(parseCpu(cpu), parseBytesAsMi(memory), parseBytesAsMi(storage));
  }

  public ResourceQuantity plus(ResourceQuantity other) {
    return new ResourceQuantity(
        cpuMilli + other.cpuMilli, memoryMi + other.memoryMi, storageMi + other.storageMi);
  }

  public ResourceQuantity minus(ResourceQuantity other) {
    return new ResourceQuantity(
        cpuMilli - other.cpuMilli, memoryMi - other.memoryMi, storageMi - other.storageMi);
  }

  public boolean fits(ResourceQuantity capacity) {
    return cpuMilli <= capacity.cpuMilli
        && memoryMi <= capacity.memoryMi
        && storageMi <= capacity.storageMi;
  }

  private static int parseCpu(String value) {
    if (value == null || value.isBlank()) {
      return 0;
    }
    String normalized = value.trim().toLowerCase(Locale.ROOT);
    if (normalized.endsWith("m")) {
      return Integer.parseInt(normalized.substring(0, normalized.length() - 1));
    }
    return (int) (Double.parseDouble(normalized) * 1000);
  }

  private static long parseBytesAsMi(String value) {
    if (value == null || value.isBlank()) {
      return 0;
    }
    String normalized = value.trim().toLowerCase(Locale.ROOT);
    try {
      if (normalized.endsWith("gi")) {
        return Long.parseLong(normalized.substring(0, normalized.length() - 2)) * 1024;
      }
      if (normalized.endsWith("mi")) {
        return Long.parseLong(normalized.substring(0, normalized.length() - 2));
      }
      if (normalized.endsWith("g")) {
        return Long.parseLong(normalized.substring(0, normalized.length() - 1)) * 953;
      }
      if (normalized.endsWith("m")) {
        return Long.parseLong(normalized.substring(0, normalized.length() - 1));
      }
      return Long.parseLong(normalized);
    } catch (NumberFormatException ex) {
      throw new KubeMiniException(ErrorCode.VALIDATION_FAILED, "Invalid resource quantity: " + value);
    }
  }
}
