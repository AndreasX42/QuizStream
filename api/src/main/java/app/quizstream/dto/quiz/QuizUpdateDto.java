package app.quizstream.dto.quiz;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record QuizUpdateDto(
        
        @NotNull UUID userId,
        @NotNull UUID quizId,
        @NotNull Integer numCorrect,
        String quizName) {
}
