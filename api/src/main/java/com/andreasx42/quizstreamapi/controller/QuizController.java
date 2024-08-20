package com.andreasx42.quizstreamapi.controller;

import com.andreasx42.quizstreamapi.dto.quiz.QuizCreateDto;
import com.andreasx42.quizstreamapi.dto.quiz.QuizOutboundDto;
import com.andreasx42.quizstreamapi.dto.quiz.QuizUpdateDto;
import com.andreasx42.quizstreamapi.exception.ErrorResponse;
import com.andreasx42.quizstreamapi.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
        ("/quizzes")
@AllArgsConstructor
@Tag(name = "Quiz Controller", description = "Endpoints to create and manage quizzes")
public class QuizController {

    private final QuizService quizService;

    // GET quiz by quiz id
    @GetMapping(value = "{quizName}/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Returns a quiz based on provided user id and quiz name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Quiz doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "200", description = "Successful retrieval of quiz", content = @Content(schema = @Schema(implementation = QuizOutboundDto.class))),
    })
    public ResponseEntity<QuizOutboundDto> getQuizByUserIdAndName(@PathVariable Long userId, @PathVariable String quizName) {

        QuizOutboundDto quiz = quizService.getQuizByUserIdAndName(userId, quizName);
        return new ResponseEntity<>(quiz, HttpStatus.OK);
    }

    // GET all quizzes by userid
    @GetMapping(value = "/users/{userId}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Retrieves paged list of quizzes of user")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all quizzes of user", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuizOutboundDto.class))))
    public ResponseEntity<Page<QuizOutboundDto>> getAllQuizzesOfUser(@PathVariable Long userId, Pageable pageable) {

        Page<QuizOutboundDto> quizzes = quizService.getAllByUserId(userId, pageable);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    // GET all quizzes
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Retrieves paged list of all quizzes")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all quizzes", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuizOutboundDto.class))))
    public ResponseEntity<Page<QuizOutboundDto>> getAllQuizzes(Pageable pageable) {

        Page<QuizOutboundDto> quizzes = quizService.getAll(pageable);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    // CREATE quiz by userid
    @PostMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Creates a quiz from provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful creation of quiz", content = @Content(schema = @Schema(implementation = QuizOutboundDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<QuizOutboundDto> createQuiz(@PathVariable Long userId, @RequestBody QuizCreateDto quizDto) {

        QuizOutboundDto createdQuiz = quizService.create(userId, quizDto);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    // UPDATE quiz by userid and quizid
    @PutMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Updates a quiz by user id, quiz name and provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of quiz", content = @Content(schema = @Schema(implementation = QuizOutboundDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<QuizOutboundDto> updateQuiz(@PathVariable Long userId,
                                                      @RequestBody QuizUpdateDto quizDto) {

        QuizOutboundDto updatedQuiz = quizService.update(userId, quizDto);
        return new ResponseEntity<>(updatedQuiz, HttpStatus.OK);
    }

    // DELETE quiz by userid and quiz name
    @DeleteMapping("{quizName}/users/{userId}")
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Deletes quiz with given user id and quiz name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful deletion of quiz", content = @Content(schema = @Schema(implementation = HttpStatus.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<HttpStatus> deleteQuiz(@PathVariable Long userId, @PathVariable String quizName) {

        quizService.delete(userId, quizName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
