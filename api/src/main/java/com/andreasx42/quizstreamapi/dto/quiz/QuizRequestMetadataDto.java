package com.andreasx42.quizstreamapi.dto.quiz;

import com.andreasx42.quizstreamapi.entity.UserQuiz;
import jakarta.validation.constraints.NotNull;

public record QuizRequestMetadataDto(
        @NotNull String videoUrl,
        @NotNull UserQuiz.Language language,
        @NotNull UserQuiz.Difficulty difficulty,
        @NotNull UserQuiz.Type type) {
}