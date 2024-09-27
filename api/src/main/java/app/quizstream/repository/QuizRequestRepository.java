package app.quizstream.repository;


import app.quizstream.entity.request.QuizRequest;
import app.quizstream.entity.request.QuizRequestId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface QuizRequestRepository extends JpaRepository<QuizRequest, QuizRequestId> {

    @Query("SELECT req " +
            "FROM QuizRequest req " +
            "WHERE req.id.userId = :userId AND req.isDeleted = false " +
            "AND (:status IS NULL OR req.status = :status)")
    Page<QuizRequest> findByUserIdAndIsDeletedFalseAndStatus(
            @Param("userId") UUID userId, @Param("status") QuizRequest.Status status, Pageable pageable);
}