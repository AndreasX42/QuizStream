package app.quizstream.dto.quiz;

import jakarta.validation.constraints.NotNull;

public record QuizLeaderboardEntry(

        @NotNull String username,
        @NotNull Long numberQuizzes,
        @NotNull Long numberAttempts,
        @NotNull Long numberQuestions,
        @NotNull Long numberCorrectAnswers,
        @NotNull String score) {

}
