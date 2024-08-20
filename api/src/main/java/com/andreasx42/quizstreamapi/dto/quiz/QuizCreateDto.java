package com.andreasx42.quizstreamapi.dto.quiz;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record QuizCreateDto(

        @NotNull String name,
        @NotNull String videoUrl,
        @NotNull Map<String, String> apiKeys) {


}