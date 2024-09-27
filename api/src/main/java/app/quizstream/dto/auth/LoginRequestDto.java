package app.quizstream.dto.auth;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(
        @NotNull String username,
        @NotNull String password) {
}
