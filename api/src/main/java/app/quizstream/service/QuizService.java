package app.quizstream.service;

import app.quizstream.dto.quiz.*;
import app.quizstream.entity.User;
import app.quizstream.entity.UserQuiz;
import app.quizstream.entity.embedding.LangchainPGEmbedding;
import app.quizstream.entity.request.QuizRequest;
import app.quizstream.util.mapper.QuizMapper;
import app.quizstream.util.mapper.QuizRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class QuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    private final UserService userService;
    private final UserQuizService userQuizService;
    private final QuizRequestService quizRequestService;
    private final QuizCreationAsyncBackendService quizCreationAsyncBackendService;
    private final QuizMapper quizMapper;
    private final QuizRequestMapper quizJobMapper;

    public QuizService(UserService userService, UserQuizService userQuizService, QuizRequestService quizJobService, QuizCreationAsyncBackendService quizCreationAsyncBackendService, QuizMapper quizMapper, QuizRequestMapper quizJobMapper) {
        this.userService = userService;
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

    public Page<QuizLeaderboardEntry> getLeaderboardData(Pageable pageable) {

        logger.warn(pageable.toString());

        List<QuizLeaderboardEntry> entries = getLeaderboardEntries();
        sortLeaderboardEntries(entries, pageable);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), entries.size());

        return new PageImpl<>(entries.subList(start, end), pageable, entries.size());
    }

    private List<QuizLeaderboardEntry> getLeaderboardEntries() {

        List<QuizLeaderboardEntry> entries = new ArrayList<>();
        List<User> users = userService.getAll();

        for (User user : users) {
            long numQuizzes = user.getQuizzes()
                    .size();

            long numAttempts = user.getQuizzes()
                    .stream()
                    .map(UserQuiz::getNumTries)
                    .mapToInt(Integer::intValue)
                    .sum();

            long numQuestions = user.getQuizzes()
                    .stream()
                    .map(UserQuiz::getNumQuestions)
                    .mapToInt(Integer::intValue)
                    .sum();

            long numCorrectAnswers = user.getQuizzes()
                    .stream()
                    .map(UserQuiz::getNumCorrect)
                    .mapToInt(Integer::intValue)
                    .sum();

            if (numQuizzes > 0 && numAttempts > 0 && numCorrectAnswers > 0) {
                double score = (double) numCorrectAnswers / numQuestions * 100.0;
                String scoreString = String.format("%.2f", score);

                entries.add(new QuizLeaderboardEntry(user.getUsername(), numQuizzes,
                        numAttempts, numQuestions, numCorrectAnswers, scoreString));
            }
        }

        return entries;
    }

    private void sortLeaderboardEntries(List<QuizLeaderboardEntry> entries, Pageable pageable) {

        // sorting entries based on the Pageable sorting criteria
        Sort sort = pageable.getSort();
        sort.forEach(order -> {
            Comparator<QuizLeaderboardEntry> comparator = Comparator.comparing(QuizLeaderboardEntry::score);

            if (order.getProperty()
                    .equals("quizzes")) {
                comparator = Comparator.comparing(QuizLeaderboardEntry::numberQuizzes);
            } else if (order.getProperty()
                    .equals("attempts")) {
                comparator = Comparator.comparing(QuizLeaderboardEntry::numberAttempts);
            } else if (order.getProperty()
                    .equals("questions")) {
                comparator = Comparator.comparing(QuizLeaderboardEntry::numberQuestions);
            } else if (order.getProperty()
                    .equals("answers")) {
                comparator = Comparator.comparing(QuizLeaderboardEntry::numberCorrectAnswers);
            }

            if (order.getDirection()==Sort.Direction.DESC) {
                comparator = comparator.reversed();
            }

            entries.sort(comparator);
        });

    }
}
