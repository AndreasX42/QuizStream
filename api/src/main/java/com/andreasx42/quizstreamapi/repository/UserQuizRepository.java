package com.andreasx42.quizstreamapi.repository;


import com.andreasx42.quizstreamapi.entity.UserQuiz;
import com.andreasx42.quizstreamapi.entity.UserQuizId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserQuizRepository extends JpaRepository<UserQuiz, UserQuizId> {

    Optional<UserQuiz> findById_UserIdAndId_QuizId(Long userId, UUID quizId);

    Page<UserQuiz> findByUser_Id(Long userId, Pageable pageable);
}