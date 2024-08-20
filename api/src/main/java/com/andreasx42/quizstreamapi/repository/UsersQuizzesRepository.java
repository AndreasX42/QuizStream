package com.andreasx42.quizstreamapi.repository;


import com.andreasx42.quizstreamapi.entity.UsersQuizzes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersQuizzesRepository extends JpaRepository<UsersQuizzes, Long> {

    Optional<UsersQuizzes> findByUserIdAndQuizName(Long studentId, String quizName);


}