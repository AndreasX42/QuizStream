package app.quizstream.util.mapper;


import app.quizstream.dto.quiz.QuizRequestDto;
import app.quizstream.dto.quiz.QuizRequestMetadataDto;
import app.quizstream.entity.request.QuizRequest;
import org.springframework.stereotype.Component;

@Component
public class QuizRequestMapper {

    public QuizRequestDto mapFromEntityOutbound(QuizRequest quizRequest) {

        QuizRequestMetadataDto metadataDto = new QuizRequestMetadataDto(
                quizRequest.getRequestMetadata()
                        .videoUrl(), quizRequest.getRequestMetadata()
                .language(), quizRequest.getRequestMetadata()
                .difficulty(), quizRequest.getRequestMetadata()
                .type());


        return new QuizRequestDto(quizRequest.getId()
                .getUserId(), quizRequest.getId()
                .getQuizName(), quizRequest.getStatus(), quizRequest.getDateCreated(), quizRequest.getQuizId(), quizRequest.getDateFinished(), quizRequest.getMessageExternal(), metadataDto);

    }
}