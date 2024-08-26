package com.andreasx42.quizstreamapi.dto.quiz;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record QuizCreateResultDto(

        @NotNull Long userId,
        @NotNull UUID quizId,
        @NotNull String quizName) {

}