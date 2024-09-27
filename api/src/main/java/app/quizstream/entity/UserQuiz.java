package app.quizstream.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import app.quizstream.entity.collection.LangchainPGCollection;


@Entity
@Table(name = "user_quiz")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserQuiz {

    public enum Type {
        MULTIPLE_CHOICE
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public enum Language {
        EN, ES, DE
    }


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

    @Column(name = "num_questions", nullable = false, columnDefinition = "int default 0")
    private int numQuestions = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private UserQuiz.Language language = Language.EN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private UserQuiz.Type type = Type.MULTIPLE_CHOICE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private UserQuiz.Difficulty difficulty = Difficulty.EASY;

    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;


}