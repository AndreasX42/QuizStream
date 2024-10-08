package app.quizstream.util.mapper;

import app.quizstream.dto.quiz.QuizCreateResultDto;
import app.quizstream.dto.quiz.QuizOutboundDto;
import app.quizstream.dto.quiz.QuizQuestionDetailsDto;
import app.quizstream.dto.quiz.VideoMetadataDto;
import app.quizstream.entity.UserQuiz;
import app.quizstream.entity.collection.CollectionMetadata;
import app.quizstream.entity.collection.LangchainPGCollection;
import app.quizstream.entity.collection.VideoMetadata;
import app.quizstream.entity.embedding.EmbeddingMetadata;
import app.quizstream.entity.embedding.LangchainPGEmbedding;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class QuizMapper {

    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(QuizMapper.class);

    public QuizMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public QuizOutboundDto convertToQuizOutboundDto(UserQuiz userQuiz) {
        try {
            LangchainPGCollection collection = userQuiz.getLangchainCollection();
            CollectionMetadata collectionMetadata = collection.getCmetadata();

            // Extract values from JSON
            LocalDate dateCreated = LocalDate.parse(userQuiz.getDateCreated()
                    .toString()
                    .replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            VideoMetadataDto videoMetadataDto = getVideoMetadataDto(collectionMetadata);

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
                    userQuiz.getNumQuestions(),
                    userQuiz.getLanguage(),
                    userQuiz.getType(),
                    userQuiz.getDifficulty(),
                    videoMetadataDto
            );
        } catch (Exception e) {
            logger.error("Failed to convert Json into Dto: {}", e.getMessage());
            throw new RuntimeException("Failed to convert Json into Dto", e);
        }
    }

    public QuizQuestionDetailsDto convertToQuizDetailsDto(LangchainPGEmbedding questionAndAnswer) {
        try {

            EmbeddingMetadata embeddingMetadata = questionAndAnswer.getCmetadata();

            return new QuizQuestionDetailsDto(
                    questionAndAnswer.getDocument(),
                    embeddingMetadata.answers()
                            .correctAnswer(),
                    embeddingMetadata.answers()
                            .wrongAnswers(),
                    embeddingMetadata.context()
            );


        } catch (Exception e) {
            logger.error("Failed to convert Json into Dto: {}", e.getMessage());
            throw new RuntimeException("Failed to convert Json into Dto", e);
        }
    }

    public QuizCreateResultDto convertToQuizOutboundDto(String jsonResponse) {
        try {
            // Parse JSON response
            JsonNode root = objectMapper.readTree(jsonResponse);

            // Extract fields from JSON
            UUID userId = UUID.fromString(root.get("user_id")
                    .asText());
            UUID quizId = UUID.fromString(root.get("quiz_id")
                    .asText());
            String name = root.get("quiz_name")
                    .asText();

            return new QuizCreateResultDto(userId, quizId, name);

        } catch (Exception e) {
            logger.error("Failed to convert Json into Dto: {}", e.getMessage());
            throw new RuntimeException("Failed to convert Json into Dto", e);
        }
    }

    private VideoMetadataDto getVideoMetadataDto(CollectionMetadata collectionMetadata) {

        VideoMetadata videoMetadata = collectionMetadata.videoMetadata();

        return new VideoMetadataDto(
                videoMetadata.title(),
                videoMetadata.source(),
                videoMetadata.thumbnailUrl(),
                videoMetadata.description(),
                videoMetadata.viewCount(),
                videoMetadata.publishDate(),
                videoMetadata.author()
        );
    }
}