package com.kubemini.api.controller;

import com.kubemini.common.api.ApiResponse;
import com.kubemini.common.error.ErrorCode;
import com.kubemini.common.error.KubeMiniException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(KubeMiniException.class)
  public ResponseEntity<ApiResponse<Void>> kubeMini(KubeMiniException ex) {
    return ResponseEntity.status(status(ex.code())).body(ApiResponse.fail(ex.code().name(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(this::fieldMessage)
            .orElse("Invalid request.");
    return ResponseEntity.badRequest().body(ApiResponse.fail(ErrorCode.VALIDATION_FAILED.name(), message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> unexpected(Exception ex) {
    return ResponseEntity.internalServerError().body(ApiResponse.fail("INTERNAL_ERROR", ex.getMessage()));
  }

  private String fieldMessage(FieldError error) {
    return error.getField() + " " + error.getDefaultMessage();
  }

  private HttpStatus status(ErrorCode code) {
    return switch (code) {
      case DEPLOYMENT_NOT_FOUND, NODE_NOT_FOUND, POD_NOT_FOUND, SERVICE_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case INSUFFICIENT_CLUSTER_RESOURCES, SCHEDULING_FAILED, CONFLICT -> HttpStatus.CONFLICT;
      case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
      case VALIDATION_FAILED -> HttpStatus.BAD_REQUEST;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}
