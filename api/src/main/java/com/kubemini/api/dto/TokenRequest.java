package com.kubemini.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank String subject, String role) {}
