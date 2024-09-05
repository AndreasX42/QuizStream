package com.andreasx42.quizstreamapi.entity.collection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;

public record VideoMetadata(@NotNull String source,
                            @NotNull String title,
                            @NotNull String description,
                            @NotNull Integer length,
                            @NotNull String author,
                            @NotNull String transcript,

                            @JsonProperty("view_count")
                            @NotNull Integer viewCount,

                            @JsonProperty("thumbnail_url")
                            @NotNull String thumbnailUrl,

                            @JsonProperty("publish_date")
                            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                            @NotNull LocalDate publishDate) implements Serializable {
}