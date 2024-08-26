package com.andreasx42.quizstreamapi.dto.quiz;

import com.andreasx42.quizstreamapi.entity.UserQuiz;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record QuizOutboundDto(

        @NotNull Long userId,
        @NotNull UUID quizId,
        @NotNull String quizName,
        @NotNull LocalDate dateCreated,
        @NotNull Integer numTries,
        @NotNull Integer numCorrect,
        @NotNull UserQuiz.Language language,
        @NotNull UserQuiz.Type type,
        @NotNull UserQuiz.Difficulty difficulty,
        @NotNull VideoMetadataDto metadata) {

}