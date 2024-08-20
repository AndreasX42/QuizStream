package com.andreasx42.quizstreamapi.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
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
}

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
class UsersQuizzesId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "quiz_name")
    private String quizName;

}