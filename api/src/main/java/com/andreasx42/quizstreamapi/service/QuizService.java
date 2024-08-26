package com.andreasx42.quizstreamapi.service;

import com.andreasx42.quizstreamapi.dto.quiz.*;
import com.andreasx42.quizstreamapi.entity.LangchainPGEmbedding;
import com.andreasx42.quizstreamapi.entity.UserQuiz;
import com.andreasx42.quizstreamapi.exception.BadBackendResponseException;
import com.andreasx42.quizstreamapi.security.config.EnvConfigs;
import com.andreasx42.quizstreamapi.util.mapper.QuizMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
    private final RestTemplate restTemplate;

    private final UserQuizService userQuizService;
    private final EnvConfigs envConfigs;
    private final QuizMapper quizMapper;


    public Page<QuizOutboundDto> getAllUserQuizzes(Long userId, Pageable pageable) {
        return userQuizService.getAllUserQuizzes(userId, pageable)
                .map(quizMapper::convertToQuizOutboundDto);
    }

    public QuizOutboundDto getQuizByUserQuizId(Long userId, UUID quizId) {
        UserQuiz userQuiz = userQuizService.getByUserQuizId(userId, quizId);
        return quizMapper.convertToQuizOutboundDto(userQuiz);
    }

    public QuizDetailsOutboundDto getQuizDetailsByUserQuizId(Long userId, UUID quizId) {
        UserQuiz userQuiz = userQuizService.getByUserQuizId(userId, quizId);
        List<LangchainPGEmbedding> questionAndAnswersList = userQuiz.getLangchainCollection()
                .getEmbeddings();

        List<QuizQuestionDetailsDto> details = questionAndAnswersList.stream()
                .map(quizMapper::convertToQuizDetailsDto)
                .toList();

        return new QuizDetailsOutboundDto(userId, quizId, details);

    }

    public QuizOutboundDto createQuizOnBackend(QuizCreateDto quizCreateDto) {

        logger.error(quizCreateDto.toString());

        // Create the request body for FastAPI
        Map<String, Object> body = Map.of(
                "user_id", quizCreateDto.userId(),
                "quiz_name", quizCreateDto.quizName(),
                "api_keys", quizCreateDto.apiKeys(),
                "youtube_url", quizCreateDto.videoUrl(),
                "language", quizCreateDto.language(),
                "type", quizCreateDto.type()
                        .toString(),
                "difficulty", quizCreateDto.difficulty()
                        .toString()
        );

        // Make the POST request
        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, new HttpHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    envConfigs.backendCreateNewQuizEndpoint,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return quizMapper.convertToQuizOutboundDto(response.getBody());

        } catch (Exception e) {
            logger.error("Backend API call failed: {}", e.getMessage());
            throw new BadBackendResponseException(e.getMessage(), QuizService.class);
        }
    }

    public QuizOutboundDto updateQuiz(QuizUpdateDto quizUpdateDto) {
        UserQuiz updatedUserQuiz = userQuizService.updateUserQuiz(quizUpdateDto);
        return quizMapper.convertToQuizOutboundDto(updatedUserQuiz);

    }

    public void deleteQuiz(Long userId, UUID quizId) {
        userQuizService.deleteByUserQuizId(userId, quizId);
    }

}
