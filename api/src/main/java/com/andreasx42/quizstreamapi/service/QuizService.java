package com.andreasx42.quizstreamapi.service;

import com.andreasx42.quizstreamapi.dto.quiz.QuizCreateDto;
import com.andreasx42.quizstreamapi.dto.quiz.QuizOutboundDto;
import com.andreasx42.quizstreamapi.dto.quiz.QuizUpdateDto;
import com.andreasx42.quizstreamapi.entity.UserQuiz;
import com.andreasx42.quizstreamapi.exception.BadBackendResponseException;
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

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuizService {

    private final String BACKEND_CREATE_QUIZ_ENDPOINT = "http://backend:8080/quizzes/new";

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
    private final RestTemplate restTemplate;

    private final UserQuizService userQuizService;
    private final QuizMapper quizMapper;


    public Page<QuizOutboundDto> getAllUserQuizzes(Long userId, Pageable pageable) {
        return userQuizService.getAllUserQuizzes(userId, pageable)
                .map(quizMapper::convertToQuizOutboundDto);
    }

    public QuizOutboundDto getQuizByUserQuizId(Long userId, UUID quizId) {
        UserQuiz userQuiz = userQuizService.getByUserQuizId(userId, quizId);
        return quizMapper.convertToQuizOutboundDto(userQuiz);
    }

    public QuizOutboundDto createQuizOnBackend(QuizCreateDto quizCreateDto) {

        // Create the request body for FastAPI
        Map<String, Object> body = Map.of(
                "user_id", quizCreateDto.userId(),
                "quiz_name", quizCreateDto.name(),
                "api_keys", quizCreateDto.apiKeys(),
                "youtube_url", quizCreateDto.videoUrl()
        );

        // Make the POST request
        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, new HttpHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    BACKEND_CREATE_QUIZ_ENDPOINT,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return quizMapper.convertToQuizOutboundDto(response.getBody());

            // TOOD: improve backend error handling
        } catch (Exception e) {
            logger.error("Backend API call failed: {}", e.getMessage());
            if (e.getMessage()
                    .contains("Internal Server Error")) {
                throw new BadBackendResponseException("Invalid API keys provided.", QuizService.class);
            }

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
