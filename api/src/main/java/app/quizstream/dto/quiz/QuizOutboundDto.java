package app.quizstream.dto.quiz;

import app.quizstream.entity.UserQuiz;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record QuizOutboundDto(

        @NotNull UUID userId,
        @NotNull UUID quizId,
        @NotNull String quizName,
        @NotNull LocalDate dateCreated,
        @NotNull Integer numTries,
        @NotNull Integer numCorrect,
        @NotNull Integer numQuestions,
        @NotNull UserQuiz.Language language,
        @NotNull UserQuiz.Type type,
        @NotNull UserQuiz.Difficulty difficulty,
        @NotNull VideoMetadataDto metadata) {

}