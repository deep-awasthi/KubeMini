package com.kubemini.api.controller;

import com.kubemini.api.dto.DeploymentRequest;
import com.kubemini.api.dto.DtoMapper;
import com.kubemini.api.dto.ScaleRequest;
import com.kubemini.application.port.PodRepository;
import com.kubemini.application.usecase.DeploymentUseCase;
import com.kubemini.common.api.ApiResponse;
import com.kubemini.domain.model.PodPhase;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deployments")
public class DeploymentController {
  private final DeploymentUseCase deployments;
  private final PodRepository pods;

  public DeploymentController(DeploymentUseCase deployments, PodRepository pods) {
    this.deployments = deployments;
    this.pods = pods;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','DEVELOPER')")
  public ApiResponse<?> create(@Valid @RequestBody DeploymentRequest request) {
    var deployment = deployments.create(request.toCommand());
    return ApiResponse.ok(DtoMapper.deployment(deployment, available(deployment.id())));
  }

  @GetMapping
  public ApiResponse<?> list() {
    return ApiResponse.ok(
        deployments.list().stream().map(d -> DtoMapper.deployment(d, available(d.id()))).toList());
  }

  @GetMapping("/{id}")
  public ApiResponse<?> get(@PathVariable String id) {
    var deployment = deployments.get(id);
    return ApiResponse.ok(DtoMapper.deployment(deployment, available(id)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','DEVELOPER')")
  public ApiResponse<?> update(@PathVariable String id, @Valid @RequestBody DeploymentRequest request) {
    var deployment = deployments.update(id, request.toCommand());
    return ApiResponse.ok(DtoMapper.deployment(deployment, available(id)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<Void> delete(@PathVariable String id) {
    deployments.delete(id);
    return ApiResponse.ok();
  }

  @PostMapping("/{id}/scale")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','DEVELOPER')")
  public ApiResponse<?> scale(@PathVariable String id, @Valid @RequestBody ScaleRequest request) {
    var deployment = deployments.scale(id, request.replicas());
    return ApiResponse.ok(DtoMapper.deployment(deployment, available(id)));
  }

  @PostMapping("/{id}/pause")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<?> pause(@PathVariable String id) {
    var deployment = deployments.pause(id);
    return ApiResponse.ok(DtoMapper.deployment(deployment, available(id)));
  }

  @PostMapping("/{id}/resume")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<?> resume(@PathVariable String id) {
    var deployment = deployments.resume(id);
    return ApiResponse.ok(DtoMapper.deployment(deployment, available(id)));
  }

  @PostMapping("/{id}/rollback")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<?> rollback(@PathVariable String id) {
    var deployment = deployments.rollback(id);
    return ApiResponse.ok(DtoMapper.deployment(deployment, available(id)));
  }

  private long available(String deploymentId) {
    return pods.findByDeploymentId(deploymentId).stream().filter(p -> p.phase() == PodPhase.RUNNING).count();
  }
}
