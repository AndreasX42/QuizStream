package com.andreasx42.quizstreamapi.service;

import com.andreasx42.quizstreamapi.dto.quiz.QuizUpdateDto;
import com.andreasx42.quizstreamapi.entity.UserQuiz;
import com.andreasx42.quizstreamapi.entity.UserQuizId;
import com.andreasx42.quizstreamapi.exception.EntityNotFoundException;
import com.andreasx42.quizstreamapi.repository.LangchainPGCollectionRepository;
import com.andreasx42.quizstreamapi.repository.UserQuizRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserQuizService {

    private final UserQuizRepository userQuizRepository;

    private final LangchainPGCollectionRepository langchainPGCollectionRepository;


    public UserQuiz getByUserQuizId(Long userId, UUID quizId) {
        return userQuizRepository.findById_UserIdAndId_QuizId(userId, quizId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(quizId), UserQuiz.class));

    }

    public Page<UserQuiz> getAllUserQuizzes(Long userId, Pageable pageable) {
        return userQuizRepository.findByUser_Id(userId, pageable);

    }

    public void deleteByUserQuizId(Long userId, UUID quizId) {
        userQuizRepository.deleteById(new UserQuizId(userId, quizId));
    }

    public UserQuiz updateUserQuiz(QuizUpdateDto data) {
        UserQuiz userQuiz = getByUserQuizId(data.userId(), data.quizId());

        userQuiz.setNumTries(data.numTries());
        userQuiz.setNumCorrect(data.numCorrect());
        userQuiz.getLangchainCollection()
                .setName(data.name());

        return userQuizRepository.save(userQuiz);
    }


}
