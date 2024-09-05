package com.andreasx42.quizstreamapi.dto.quiz;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record QuizDetailsOutboundDto(

        @NotNull Long userId,
        @NotNull UUID quizId,
        @NotNull List<QuizQuestionDetailsDto> questionAnswersList) implements Serializable {

}