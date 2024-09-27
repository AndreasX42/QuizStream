package app.quizstream.dto.quiz;

import app.quizstream.entity.UserQuiz;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record QuizCreateRequestDto(

        @NotNull UUID userId,
        @NotNull String quizName,
        @NotNull String videoUrl,
        @NotNull Map<String, String> apiKeys,
        @NotNull UserQuiz.Language language,
        @NotNull UserQuiz.Type type,
        @NotNull UserQuiz.Difficulty difficulty) {

}