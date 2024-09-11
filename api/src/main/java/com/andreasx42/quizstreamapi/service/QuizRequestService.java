package com.andreasx42.quizstreamapi.service;

import com.andreasx42.quizstreamapi.dto.quiz.QuizCreateRequestDto;
import com.andreasx42.quizstreamapi.dto.quiz.QuizDeleteRequestDto;
import com.andreasx42.quizstreamapi.dto.quiz.QuizRequestDto;
import com.andreasx42.quizstreamapi.entity.request.QuizRequest;
import com.andreasx42.quizstreamapi.entity.request.QuizRequestId;
import com.andreasx42.quizstreamapi.entity.request.RequestMetadata;
import com.andreasx42.quizstreamapi.exception.EntityNotFoundException;
import com.andreasx42.quizstreamapi.repository.QuizRequestRepository;
import com.andreasx42.quizstreamapi.util.mapper.QuizRequestMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuizRequestService {

    private final QuizRequestRepository quizRequestRepository;
    private final QuizRequestMapper quizRequestMapper;
    private final UserService userService;

    public QuizRequestService(QuizRequestRepository quizRequestRepository, QuizRequestMapper quizRequestMapper,
                              UserService userService) {
        this.quizRequestRepository = quizRequestRepository;
        this.quizRequestMapper = quizRequestMapper;
        this.userService = userService;
    }

    public QuizRequest createQuizRequest(QuizCreateRequestDto quizCreateDto) {

        RequestMetadata requestMetadata = new RequestMetadata(
                quizCreateDto.videoUrl(),
                quizCreateDto.language(),
                quizCreateDto.difficulty(),
                quizCreateDto.type());

        QuizRequest newRequest = QuizRequest.builder()
                .id(new QuizRequestId(quizCreateDto.userId(), quizCreateDto.quizName()
                        .toLowerCase()))
                .user(userService.getById(quizCreateDto.userId()))
                .requestMetadata(requestMetadata)
                .messageInternal(null)
                .messageExternal(null)
                .build();

        return quizRequestRepository.save(newRequest);
    }

    public void updateQuizRequest(QuizRequest quizRequest) {
        quizRequestRepository.save(quizRequest);
    }

    public Page<QuizRequestDto> getRequestsForUserId(Long userId, QuizRequest.Status status, Pageable pageable) {

        Page<QuizRequest> quizRequests = quizRequestRepository.findByUserIdAndIsDeletedFalseAndStatus(userId, status,
                pageable);

        return quizRequests.map(quizRequestMapper::mapFromEntityOutbound);
    }

    public Optional<QuizRequest> getRequestByQuizRequestId(Long userId, String quizName) {
        return quizRequestRepository.findById(new QuizRequestId(userId, quizName));
    }

    public void deleteRequestForUser(QuizDeleteRequestDto quizDeleteRequestDto) {

        QuizRequestId id = new QuizRequestId(quizDeleteRequestDto.userId(),
                quizDeleteRequestDto.quizName());

        QuizRequest request = quizRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id.toString(), QuizRequest.class));

        request.setDeleted(true);

        quizRequestRepository.save(request);

    }
}
