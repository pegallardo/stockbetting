package com.morpheus.stockbetting.dto.request;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
    @NotBlank String username,
    @NotBlank String password
) {}

