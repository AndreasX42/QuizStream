package com.andreasx42.quizstreamapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;


@Entity
@Table(name = "users_quizzes")
@Getter
@Setter
@NoArgsConstructor
public class UsersQuizzes {

    @EmbeddedId
    private UsersQuizzesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId("quizName")
    @JoinColumn(name = "quiz_name", nullable = false, referencedColumnName = "name")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private LangchainPGCollection quiz;

    @Column(name = "collection_id", nullable = false)
    private UUID collectionId;

    @Column(name = "num_tries", nullable = false, columnDefinition = "int default 0")
    private int numTries = 0;

    @Column(name = "num_correct_answers", nullable = false, columnDefinition = "int default 0")
    private int numCorrectAnswers = 0;

}