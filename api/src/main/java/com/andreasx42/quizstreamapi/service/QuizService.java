package com.andreasx42.quizstreamapi.service;

import com.andreasx42.quizstreamapi.dto.quiz.*;
import com.andreasx42.quizstreamapi.entity.UserQuiz;
import com.andreasx42.quizstreamapi.entity.embedding.LangchainPGEmbedding;
import com.andreasx42.quizstreamapi.entity.request.QuizRequest;
import com.andreasx42.quizstreamapi.util.mapper.QuizMapper;
import com.andreasx42.quizstreamapi.util.mapper.QuizRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    private final UserQuizService userQuizService;
    private final QuizRequestService quizRequestService;
    private final QuizCreationAsyncBackendService quizCreationAsyncBackendService;
    private final QuizMapper quizMapper;
    private final QuizRequestMapper quizJobMapper;

    public QuizService(UserQuizService userQuizService, QuizRequestService quizJobService, QuizCreationAsyncBackendService quizCreationAsyncBackendService, QuizMapper quizMapper, QuizRequestMapper quizJobMapper) {
        this.userQuizService = userQuizService;
        this.quizRequestService = quizJobService;
        this.quizCreationAsyncBackendService = quizCreationAsyncBackendService;
        this.quizMapper = quizMapper;
        this.quizJobMapper = quizJobMapper;
    }


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

    public QuizRequestDto createQuiz(QuizCreateRequestDto quizCreateDto) {

        logger.info("Creating quiz '{}' for user with id '{}', '{}', '{}', '{}'.",
                quizCreateDto.quizName(), quizCreateDto.userId(), quizCreateDto.language()
                        .name(), quizCreateDto.difficulty()
                        .name(), quizCreateDto.type()
                        .name());

        // create quizJob in table to keep track of status
        QuizRequest quizJob = this.quizRequestService.createQuizRequest(quizCreateDto);

        // call backend asynchronously and update quizJob when done
        quizCreationAsyncBackendService.createQuiz(quizCreateDto, quizJob);

        return quizJobMapper.mapFromEntityOutbound(quizJob);
    }

    public QuizOutboundDto updateQuiz(QuizUpdateDto quizUpdateDto) {
        UserQuiz updatedUserQuiz = userQuizService.updateUserQuiz(quizUpdateDto);
        return quizMapper.convertToQuizOutboundDto(updatedUserQuiz);

    }

    public void deleteQuiz(Long userId, UUID quizId) {
        userQuizService.deleteByUserQuizId(userId, quizId);
    }

}
