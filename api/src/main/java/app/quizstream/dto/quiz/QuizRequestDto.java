package app.quizstream.dto.quiz;

import app.quizstream.entity.request.QuizRequest;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizRequestDto(

        @NotNull UUID userId,
        @NotNull String quizName,
        @NotNull QuizRequest.Status status,
        @NotNull LocalDateTime dateCreated,
        @Nullable UUID quizId,
        @Nullable LocalDateTime dateFinished,
        @Nullable String errorMessage,
        @NotNull QuizRequestMetadataDto metadata) {

}

