package com.andreasx42.quizstreamapi.dto.quiz;

import com.andreasx42.quizstreamapi.entity.UserQuiz;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record QuizCreateRequestDto(

        @NotNull Long userId,
        @NotNull String quizName,
        @NotNull String videoUrl,
        @NotNull Map<String, String> apiKeys,
        @NotNull UserQuiz.Language language,
        @NotNull UserQuiz.Type type,
        @NotNull UserQuiz.Difficulty difficulty) {

}