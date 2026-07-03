package com.kubemini.api.controller;

import com.kubemini.api.dto.DtoMapper;
import com.kubemini.api.dto.ServiceRequest;
import com.kubemini.application.usecase.ServiceUseCase;
import com.kubemini.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {
  private final ServiceUseCase services;

  public ServiceController(ServiceUseCase services) {
    this.services = services;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','DEVELOPER')")
  public ApiResponse<?> create(@Valid @RequestBody ServiceRequest request) {
    return ApiResponse.ok(DtoMapper.service(services.create(request.toCommand())));
  }

  @GetMapping
  public ApiResponse<?> list() {
    return ApiResponse.ok(services.list().stream().map(DtoMapper::service).toList());
  }
}
