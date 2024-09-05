package com.andreasx42.quizstreamapi.dto.quiz;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public record QuizQuestionDetailsDto(
        @NotNull String question,
        @NotNull String correctAnswer,
        @NotNull List<String> wrongAnswers,
        @NotNull String context) implements Serializable {
}