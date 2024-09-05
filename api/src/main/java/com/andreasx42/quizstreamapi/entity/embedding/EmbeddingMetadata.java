package com.andreasx42.quizstreamapi.entity.embedding;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record EmbeddingMetadata(
        @NotNull String id,
        @NotNull String context,
        @NotNull Answers answers,

        @JsonProperty("start_index")
        @NotNull Integer startIndex,

        @JsonProperty("end_index")
        @NotNull Integer endIndex) {
}