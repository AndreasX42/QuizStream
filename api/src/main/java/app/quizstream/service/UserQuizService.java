package app.quizstream.service;

import app.quizstream.dto.quiz.QuizUpdateDto;
import app.quizstream.entity.UserQuiz;
import app.quizstream.entity.UserQuizId;
import app.quizstream.exception.EntityNotFoundException;
import app.quizstream.repository.UserQuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class UserQuizService {

    private final UserQuizRepository userQuizRepository;

    public UserQuizService(UserQuizRepository userQuizRepository) {
        this.userQuizRepository = userQuizRepository;
    }


    public UserQuiz getByUserQuizId(UUID userId, UUID quizId) {
        return userQuizRepository.findById_UserIdAndId_QuizId(userId, quizId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(quizId), UserQuiz.class));
    }

    public Page<UserQuiz> getAllUserQuizzes(UUID userId, Pageable pageable) {
        return userQuizRepository.findByUser_Id(userId, pageable);

    }

    public void deleteByUserQuizId(UUID userId, UUID quizId) {
        userQuizRepository.deleteById(new UserQuizId(userId, quizId));
    }

    public void deleteByUserQuizEntity(UserQuiz userQuiz) {
        userQuizRepository.delete(userQuiz);
    }

    public UserQuiz updateUserQuiz(QuizUpdateDto data) {
        UserQuiz userQuiz = getByUserQuizId(data.userId(), data.quizId());

        userQuiz.setNumCorrect(userQuiz.getNumCorrect() + data.numCorrect());

        userQuiz.setNumTries(userQuiz.getNumTries() + 1);

        if (Objects.nonNull(data.quizName()) && !data.quizName()
                .isBlank()) {
            userQuiz.getLangchainCollection()
                    .setName(data.quizName());
        }

        return userQuizRepository.save(userQuiz);
    }


}
