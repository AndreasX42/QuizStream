package com.andreasx42.quizstreamapi.dto.auth;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(
        @NotNull String username,
        @NotNull String password) {
}
