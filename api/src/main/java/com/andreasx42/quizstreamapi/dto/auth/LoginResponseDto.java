package com.andreasx42.quizstreamapi.dto.auth;

import jakarta.validation.constraints.NotNull;

public record LoginResponseDto(
        @NotNull Long userId,
        @NotNull String userName,
        @NotNull String email,
        @NotNull String role) {
}
