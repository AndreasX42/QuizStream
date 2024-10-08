package app.quizstream.repository;


import app.quizstream.entity.UserQuiz;
import app.quizstream.entity.UserQuizId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserQuizRepository extends JpaRepository<UserQuiz, UserQuizId> {

    Optional<UserQuiz> findById_UserIdAndId_QuizId(UUID userId, UUID quizId);

    Page<UserQuiz> findByUser_Id(UUID userId, Pageable pageable);
}