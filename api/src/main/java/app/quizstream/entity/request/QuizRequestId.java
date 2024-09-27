package app.quizstream.entity.request;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class QuizRequestId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "quiz_name")
    private String quizName;

}