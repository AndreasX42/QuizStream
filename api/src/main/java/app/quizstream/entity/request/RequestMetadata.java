package app.quizstream.entity.request;

import app.quizstream.entity.UserQuiz;
import jakarta.validation.constraints.NotNull;

public record RequestMetadata(
        @NotNull String videoUrl,
        @NotNull UserQuiz.Language language,
        @NotNull UserQuiz.Difficulty difficulty,
        @NotNull UserQuiz.Type type) {
}