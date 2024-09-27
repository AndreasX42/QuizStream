package app.quizstream.dto.quiz;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VideoMetadataDto(
        @NotNull String title,
        @NotNull String videoUrl,
        @NotNull String thumbnailUrl,
        @NotNull String description,
        @NotNull Integer viewers,
        @NotNull LocalDate publishDate,
        @NotNull String author
) {}