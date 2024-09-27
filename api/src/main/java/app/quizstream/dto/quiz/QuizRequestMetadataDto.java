package app.quizstream.dto.quiz;

import app.quizstream.entity.UserQuiz;
import jakarta.validation.constraints.NotNull;

public record QuizRequestMetadataDto(
        @NotNull String videoUrl,
        @NotNull UserQuiz.Language language,
        @NotNull UserQuiz.Difficulty difficulty,
        @NotNull UserQuiz.Type type) {
}