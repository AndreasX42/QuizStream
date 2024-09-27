package app.quizstream.dto.quiz;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record QuizCreateResultDto(

        @NotNull UUID userId,
        @NotNull UUID quizId,
        @NotNull String quizName) {

}