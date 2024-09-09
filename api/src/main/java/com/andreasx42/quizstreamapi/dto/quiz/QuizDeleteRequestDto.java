package com.andreasx42.quizstreamapi.dto.quiz;

import jakarta.validation.constraints.NotNull;

public record QuizDeleteRequestDto(

        @NotNull Long userId,
        @NotNull String quizName) {

}

