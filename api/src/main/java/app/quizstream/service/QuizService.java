package app.quizstream.service;

import app.quizstream.dto.quiz.*;
import app.quizstream.entity.UserQuiz;
import app.quizstream.entity.embedding.LangchainPGEmbedding;
import app.quizstream.entity.request.QuizRequest;
import app.quizstream.util.mapper.QuizMapper;
import app.quizstream.util.mapper.QuizRequestMapper;
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


    public Page<QuizOutboundDto> getAllUserQuizzes(UUID userId, Pageable pageable) {
        return userQuizService.getAllUserQuizzes(userId, pageable)
                .map(quizMapper::convertToQuizOutboundDto);
    }

    public QuizOutboundDto getQuizByUserQuizId(UUID userId, UUID quizId) {
        UserQuiz userQuiz = userQuizService.getByUserQuizId(userId, quizId);
        return quizMapper.convertToQuizOutboundDto(userQuiz);
    }

    public QuizDetailsOutboundDto getQuizDetailsByUserQuizId(UUID userId, UUID quizId) {
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

    public void deleteQuiz(UUID userId, UUID quizId) {
        userQuizService.deleteByUserQuizId(userId, quizId);
    }

}
