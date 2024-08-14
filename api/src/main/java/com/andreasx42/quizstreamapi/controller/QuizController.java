package com.andreasx42.quizstreamapi.controller;

/*
@RestController
@RequestMapping("/quizzes")
@AllArgsConstructor
@Tag(name = "Quiz Controller", description = "Endpoints to create and manage quizzes")
public class QuizController {

    private final QuizService quizService;
    private final QuizMapper quizMapper;

    // GET quiz by quiz id
    @GetMapping(value = "/{quizId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns a quiz based on provided quiz ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Quiz doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "200", description = "Successful retrieval of quiz", content = @Content(schema = @Schema(implementation = QuizDto.class))),
    })
    public ResponseEntity<QuizDto> getQuizById(@PathVariable Long quizId) {

        QuizDto quizDto = quizMapper.mapFromEntity(quizService.getById(quizId));
        return new ResponseEntity<>(quizDto, HttpStatus.OK);
    }

    // GET all quizzes by userid
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves paged list of quizzes of user")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all quizzes of user", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuizDto.class))))
    public ResponseEntity<Page<QuizDto>> getAllQuizzesOfUser(@PathVariable Long userId, Pageable pageable) {

        Page<QuizDto> quizzes = quizService.getAllByUserId(userId, pageable);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    // GET all quizzes
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves paged list of quizzes")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all quizzes", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuizDto.class))))
    public ResponseEntity<Page<QuizDto>> getAllQuizzes(Pageable pageable) {

        Page<QuizDto> quizzes = quizService.getAll(pageable);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    // CREATE quiz by userid
    @PostMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Creates a quiz from provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful creation of quiz", content = @Content(schema = @Schema(implementation = QuizDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<QuizDto> createQuiz(@PathVariable Long userId, @RequestBody QuizDto quizDto) {

        quizDto = quizService.create(userId, quizDto);
        return new ResponseEntity<>(quizDto, HttpStatus.CREATED);
    }

    // UPDATE quiz by userid and quizid
    @PutMapping(value = "/user/{userId}/quiz/{quizId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Updates a quiz by user and quiz IDs and provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of quiz", content = @Content(schema = @Schema(implementation = QuizDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<QuizDto> updateQuiz(@PathVariable Long userId, @PathVariable Long quizId,
                                              @RequestBody QuizDto quizDto) {

        quizDto = quizService.update(quizId, quizDto);
        return new ResponseEntity<>(quizDto, HttpStatus.OK);
    }

    // DELETE quiz by userid and quizid
    @DeleteMapping("/user/{userId}/quiz/{quizId}")
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Deletes quiz with given user and quiz IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful deletion of quiz", content = @Content(schema = @Schema(implementation = HttpStatus.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<HttpStatus> deleteQuiz(@PathVariable Long userId, @PathVariable Long quizId) {

        quizService.delete(quizId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
*/