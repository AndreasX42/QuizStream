package com.andreasx42.quizstreamapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "user_quiz")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserQuiz {

    @EmbeddedId
    private UserQuizId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId("quizId")
    @JoinColumn(name = "quiz_id", nullable = false, referencedColumnName = "uuid")
    private LangchainPGCollection langchainCollection;

    @Column(name = "num_tries", nullable = false, columnDefinition = "int default 0")
    private int numTries = 0;

    @Column(name = "num_correct", nullable = false, columnDefinition = "int default 0")
    private int numCorrect = 0;

}