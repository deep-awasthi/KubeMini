package com.kubemini.api.controller;

import com.kubemini.api.dto.DtoMapper;
import com.kubemini.api.dto.NodeRequest;
import com.kubemini.application.usecase.NodeUseCase;
import com.kubemini.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/nodes")
public class NodeController {
  private final NodeUseCase nodes;

  public NodeController(NodeUseCase nodes) {
    this.nodes = nodes;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<?> register(@Valid @RequestBody NodeRequest request) {
    return ApiResponse.ok(DtoMapper.node(nodes.register(request.toCommand())));
  }

  @GetMapping
  public ApiResponse<?> list() {
    return ApiResponse.ok(nodes.list().stream().map(DtoMapper::node).toList());
  }

  @GetMapping("/{id}")
  public ApiResponse<?> get(@PathVariable String id) {
    return ApiResponse.ok(DtoMapper.node(nodes.get(id)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<Void> remove(@PathVariable String id) {
    nodes.remove(id);
    return ApiResponse.ok();
  }

  @PostMapping("/{id}/drain")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<?> drain(@PathVariable String id) {
    return ApiResponse.ok(DtoMapper.node(nodes.drain(id)));
  }

  @PostMapping("/{id}/cordon")
  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
  public ApiResponse<?> cordon(@PathVariable String id) {
    return ApiResponse.ok(DtoMapper.node(nodes.cordon(id)));
  }
}
