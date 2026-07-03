package com.kubemini.api.controller;

import com.kubemini.api.dto.DtoMapper;
import com.kubemini.application.usecase.PodUseCase;
import com.kubemini.common.api.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pods")
public class PodController {
  private final PodUseCase pods;

  public PodController(PodUseCase pods) {
    this.pods = pods;
  }

  @GetMapping
  public ApiResponse<?> list() {
    return ApiResponse.ok(pods.list().stream().map(DtoMapper::pod).toList());
  }

  @GetMapping("/{id}")
  public ApiResponse<?> get(@PathVariable String id) {
    return ApiResponse.ok(DtoMapper.pod(pods.get(id)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<Void> delete(@PathVariable String id) {
    pods.delete(id);
    return ApiResponse.ok();
  }

  @PostMapping("/{id}/restart")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<?> restart(@PathVariable String id) {
    return ApiResponse.ok(DtoMapper.pod(pods.restart(id)));
  }
}
