package com.kubemini.api.controller;

import com.kubemini.api.dto.TokenRequest;
import com.kubemini.api.security.JwtService;
import com.kubemini.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final JwtService jwtService;

  public AuthController(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @PostMapping("/token")
  public ApiResponse<?> token(@Valid @RequestBody TokenRequest request) {
    return ApiResponse.ok(Map.of("token", jwtService.issue(request.subject(), request.role())));
  }
}
