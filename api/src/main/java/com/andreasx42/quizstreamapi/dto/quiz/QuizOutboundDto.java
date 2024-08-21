package com.andreasx42.quizstreamapi.dto.quiz;

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
        @NotNull VideoMetadataDto metadata) {

}