package com.andreasx42.quizstreamapi.dto.quiz;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record QuizUpdateDto(
        @NotNull String name,
        @NotNull LocalDateTime dateCreated,
        @NotNull Integer numTries,
        @NotNull Double numCorrectAnswers) {
}
