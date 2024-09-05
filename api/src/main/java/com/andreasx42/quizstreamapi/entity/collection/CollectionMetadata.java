package com.andreasx42.quizstreamapi.entity.collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record CollectionMetadata(

        @JsonProperty("video_metadata")
        @NotNull VideoMetadata videoMetadata) implements Serializable {

}