package app.quizstream.service;

import app.quizstream.dto.quiz.QuizCreateRequestDto;
import app.quizstream.dto.quiz.QuizDeleteRequestDto;
import app.quizstream.dto.quiz.QuizRequestDto;
import app.quizstream.entity.request.QuizRequest;
import app.quizstream.entity.request.QuizRequestId;
import app.quizstream.entity.request.RequestMetadata;
import app.quizstream.exception.EntityNotFoundException;
import app.quizstream.repository.QuizRequestRepository;
import app.quizstream.util.mapper.QuizRequestMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    public Page<QuizRequestDto> getRequestsForUserId(UUID userId, QuizRequest.Status status, Pageable pageable) {

        Page<QuizRequest> quizRequests = quizRequestRepository.findByUserIdAndIsDeletedFalseAndStatus(userId, status,
                pageable);

        return quizRequests.map(quizRequestMapper::mapFromEntityOutbound);
    }

    public Optional<QuizRequest> getRequestByQuizRequestId(UUID userId, String quizName) {
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
