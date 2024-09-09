package com.andreasx42.quizstreamapi.dto.quiz;

import com.andreasx42.quizstreamapi.entity.QuizRequest;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizRequestDto(

        @NotNull Long userId,
        @NotNull String quizName,
        @NotNull QuizRequest.Status status,
        @NotNull LocalDateTime dateCreated,
        @Nullable UUID quizId,
        @Nullable LocalDateTime dateFinished,
        @Nullable String errorMessage) {

}

