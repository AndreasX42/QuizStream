package com.andreasx42.quizstreamapi.exception;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public record ErrorResponse(

        @NotNull String path,
        @NotNull List<String> messages,
        @NotNull int statusCode,
        @NotNull LocalDateTime localDateTime) {

}