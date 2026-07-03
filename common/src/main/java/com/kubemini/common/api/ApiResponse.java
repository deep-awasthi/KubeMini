package com.kubemini.common.api;

import java.time.Instant;

public record ApiResponse<T>(boolean success, Instant timestamp, T data, ApiError error) {
  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, Instant.now(), data, null);
  }

  public static ApiResponse<Void> ok() {
    return ok(null);
  }

  public static ApiResponse<Void> fail(String code, String message) {
    return new ApiResponse<>(false, Instant.now(), null, new ApiError(code, message));
  }
}
