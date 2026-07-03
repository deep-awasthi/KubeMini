package com.kubemini.common.error;

public class KubeMiniException extends RuntimeException {
  private final ErrorCode code;

  public KubeMiniException(ErrorCode code, String message) {
    super(message);
    this.code = code;
  }

  public ErrorCode code() {
    return code;
  }
}
