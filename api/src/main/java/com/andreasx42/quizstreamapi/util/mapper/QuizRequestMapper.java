package com.andreasx42.quizstreamapi.util.mapper;


import com.andreasx42.quizstreamapi.dto.quiz.QuizRequestDto;
import com.andreasx42.quizstreamapi.entity.QuizRequest;
import org.springframework.stereotype.Component;

@Component
public class QuizRequestMapper {

    public QuizRequestDto mapFromEntityOutbound(QuizRequest quizJob) {
        return new QuizRequestDto(quizJob.getId()
                .getUserId(), quizJob.getId()
                .getQuizName(), quizJob.getStatus(), quizJob.getDateCreated(), quizJob.getQuizId(), quizJob.getDateFinished(), quizJob.getMessageExternal());

    }
}