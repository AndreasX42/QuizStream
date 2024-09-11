package com.andreasx42.quizstreamapi.entity.request;

import com.andreasx42.quizstreamapi.entity.UserQuiz;
import jakarta.validation.constraints.NotNull;

public record RequestMetadata(
        @NotNull String videoUrl,
        @NotNull UserQuiz.Language language,
        @NotNull UserQuiz.Difficulty difficulty,
        @NotNull UserQuiz.Type type) {
}