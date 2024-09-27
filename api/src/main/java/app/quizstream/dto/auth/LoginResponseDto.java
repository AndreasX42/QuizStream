package app.quizstream.dto.auth;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LoginResponseDto(
        @NotNull UUID userId,
        @NotNull String userName,
        @NotNull String email,
        @NotNull String role) {
}
