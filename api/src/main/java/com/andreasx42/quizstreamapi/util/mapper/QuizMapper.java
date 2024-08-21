package com.andreasx42.quizstreamapi.util.mapper;

import com.andreasx42.quizstreamapi.dto.quiz.QuizOutboundDto;
import com.andreasx42.quizstreamapi.dto.quiz.VideoMetadataDto;
import com.andreasx42.quizstreamapi.entity.LangchainPGCollection;
import com.andreasx42.quizstreamapi.entity.UserQuiz;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@AllArgsConstructor
public class QuizMapper {

    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(QuizMapper.class);

    public QuizOutboundDto convertToQuizOutboundDto(UserQuiz userQuiz) {
        try {
            LangchainPGCollection collection = userQuiz.getLangchainCollection();

            JsonNode cmetadataNode = objectMapper.readTree(collection.getCmetadata());

            // Extract values from JSON
            LocalDate dateCreated = LocalDate.parse(cmetadataNode.get("date_created")
                    .asText()
                    .replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            VideoMetadataDto videoMetadataDto = getVideoMetadataDto(cmetadataNode);

            // Map to QuizOutboundDto
            return new QuizOutboundDto(
                    userQuiz.getId()
                            .getUserId(),
                    userQuiz.getId()
                            .getQuizId(),
                    collection.getName(),
                    dateCreated,
                    userQuiz.getNumTries(),
                    userQuiz.getNumCorrect(),
                    videoMetadataDto
            );
        } catch (Exception e) {
            logger.error("Failed to convert Json into Dto: {}", e.getMessage());
            throw new RuntimeException("Failed to convert Json into Dto", e);
        }
    }

    public QuizOutboundDto convertToQuizOutboundDto(String jsonResponse) {
        try {
            // Parse JSON response
            JsonNode root = objectMapper.readTree(jsonResponse);

            // Extract fields from JSON
            Long userId = root.get("user_id")
                    .asLong();
            UUID quizId = UUID.fromString(root.get("quiz_id")
                    .asText());
            String name = root.get("quiz_name")
                    .asText();
            LocalDate dateCreated = LocalDate.parse(root.get("date_created")
                    .asText()
                    .replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            int numTries = 0;
            int numCorrect = 0;

            VideoMetadataDto videoMetadataDto = getVideoMetadataDto(root);
            
            return new QuizOutboundDto(userId, quizId, name, dateCreated, numTries, numCorrect, videoMetadataDto);

        } catch (Exception e) {
            logger.error("Failed to convert Json into Dto: {}", e.getMessage());
            throw new RuntimeException("Failed to convert Json into Dto", e);
        }
    }

    private VideoMetadataDto getVideoMetadataDto(JsonNode data) {
        // Extract video metadata
        JsonNode videoMetadataNode = data.get("video_metadata");
        VideoMetadataDto metadata = new VideoMetadataDto(
                videoMetadataNode.get("title")
                        .asText(),
                videoMetadataNode.get("source")
                        .asText(),
                videoMetadataNode.get("thumbnail_url")
                        .asText(),
                videoMetadataNode.get("description")
                        .asText(),
                videoMetadataNode.get("view_count")
                        .asInt(),
                LocalDate.parse(videoMetadataNode.get("publish_date")
                        .asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                videoMetadataNode.get("author")
                        .asText()
        );
        return metadata;
    }
}