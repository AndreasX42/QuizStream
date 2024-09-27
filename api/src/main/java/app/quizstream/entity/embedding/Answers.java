package app.quizstream.entity.embedding;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Answers(
        @JsonProperty("correct_answer")
        String correctAnswer,

        @JsonProperty("wrong_answers")
        List<String> wrongAnswers) {
}
