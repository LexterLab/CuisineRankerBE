package com.cranker.cranker.authentication.jwt;

import jakarta.validation.constraints.NotBlank;

public record JwtRefreshRequestDTO(@NotBlank String refreshToken) {}
