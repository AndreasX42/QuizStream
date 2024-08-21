package com.andreasx42.quizstreamapi.entity;

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
public class UserQuizId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "quiz_id")
    private UUID quizId;

}