package com.andreasx42.quizstreamapi.dto.quiz;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record QuizOutboundDto(

        @NotNull String name,
        @NotNull LocalDateTime dateCreated,
        @NotNull Integer numTries,
        @NotNull Double accuracy,
        @NotNull VideoMetaDataDto metadata) {


}

record VideoMetaDataDto(
        @NotNull String title,
        @NotNull String videoUrl,
        @NotNull String thumbnailUrl,
        @NotNull String description,
        @NotNull Integer viewers,
        @NotNull LocalDate publishDate,
        @NotNull String author
) {}