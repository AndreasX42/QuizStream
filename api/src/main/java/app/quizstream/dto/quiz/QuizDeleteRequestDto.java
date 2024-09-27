package app.quizstream.dto.quiz;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record QuizDeleteRequestDto(

        @NotNull UUID userId,
        @NotNull String quizName) {

}

