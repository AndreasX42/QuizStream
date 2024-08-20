package com.andreasx42.quizstreamapi.service;

import com.andreasx42.quizstreamapi.dto.quiz.QuizCreateDto;
import com.andreasx42.quizstreamapi.dto.quiz.QuizOutboundDto;
import com.andreasx42.quizstreamapi.dto.quiz.QuizUpdateDto;
import com.andreasx42.quizstreamapi.entity.UsersQuizzes;
import com.andreasx42.quizstreamapi.exception.EntityNotFoundException;
import com.andreasx42.quizstreamapi.repository.UsersQuizzesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuizService {

    private final UsersQuizzesRepository usersQuizzesRepository;

    public QuizService(UsersQuizzesRepository usersQuizzesRepository) {
        this.usersQuizzesRepository = usersQuizzesRepository;
    }

    public QuizOutboundDto getQuizByUserIdAndName(Long userId, String quizName) {
        UUID quizId = getQuizId(userId, quizName);

        return null;
    }

    public void delete(Long userId, String quizName) {
        UUID quizId = getQuizId(userId, quizName);
    }

    private UUID getQuizId(Long userId, String quizName) {
        UsersQuizzes quizData = usersQuizzesRepository.findByUserIdAndQuizName(userId, quizName)
                .orElseThrow(() -> new EntityNotFoundException(quizName, UsersQuizzes.class));

        return null;

    }

    public Page<QuizOutboundDto> getAllByUserId(Long userId, Pageable pageable) {
        return null;

    }

    public Page<QuizOutboundDto> getAll(Pageable pageable) {
        return null;

    }

    public QuizOutboundDto create(Long userId, QuizCreateDto quizDto) {
        return null;

    }

    public QuizOutboundDto update(Long userId, QuizUpdateDto quizDto) {
        return null;

    }
}
