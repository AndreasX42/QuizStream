package app.quizstream.entity.request;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import app.quizstream.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quiz_requests")
public class QuizRequest {

    public enum Status {
        CREATING, FINISHED, FAILED
    }

    @EmbeddedId
    private QuizRequestId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Column(name = "quiz_id", nullable = true, updatable = true)
    private UUID quizId;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false, updatable = true)
    private boolean isDeleted = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = true)
    private QuizRequest.Status status = Status.CREATING;

    @Builder.Default
    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    @Column(name = "date_finished", nullable = true, updatable = true)
    private LocalDateTime dateFinished;

    @Builder.Default
    @Column(name = "message_int", nullable = true, updatable = true, columnDefinition = "TEXT")
    private String messageInternal = null;

    @Builder.Default
    @Column(name = "message_ext", nullable = true, updatable = true, columnDefinition = "TEXT")
    private String messageExternal = null;

    @Type(JsonType.class)
    @Column(columnDefinition = "json", updatable = false)
    private RequestMetadata requestMetadata;

    public void markAsFinished(Status status, UUID quizId, String messageInternal, String messageExternal) {
        this.status = status;
        this.quizId = quizId;
        this.messageInternal = messageInternal;
        this.messageExternal = messageExternal;
        this.dateFinished = LocalDateTime.now();
    }

}