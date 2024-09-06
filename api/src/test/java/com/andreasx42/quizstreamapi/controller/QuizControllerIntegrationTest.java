package com.andreasx42.quizstreamapi.controller;

import com.andreasx42.quizstreamapi.dto.auth.LoginRequestDto;
import com.andreasx42.quizstreamapi.dto.auth.LoginResponseDto;
import com.andreasx42.quizstreamapi.dto.quiz.*;
import com.andreasx42.quizstreamapi.entity.User;
import com.andreasx42.quizstreamapi.entity.UserQuiz;
import com.andreasx42.quizstreamapi.repository.UserRepository;
import com.andreasx42.quizstreamapi.security.config.EnvConfigs;
import com.andreasx42.quizstreamapi.service.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@Disabled
@Commit
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QuizControllerIntegrationTest {

    @Value("${api.key.test}")
    private String apiKeyTest;

    private final QuizService quizService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EnvConfigs envConfigs;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;


    @Autowired
    public QuizControllerIntegrationTest(QuizService quizService, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, EnvConfigs envConfigs, ObjectMapper objectMapper, MockMvc mockMvc) {
        this.quizService = quizService;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.envConfigs = envConfigs;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

    private User testUser;
    private String testUserJWT;
    private QuizCreateResultDto quizCreateData;
    private QuizOutboundDto quizOutboundData;

    @BeforeAll
    public void setUp() {
        setUpUserAccount();
    }

    private void setUpUserAccount() {
        String userName = "John_Doe_2";
        String userPassword = userName + "_password";
        this.testUser = new User();
        this.testUser.setUsername(userName);
        this.testUser.setEmail(userName + "@mail.com");
        this.testUser.setPassword(bCryptPasswordEncoder.encode(userPassword));
        this.testUser.setRole(User.Role.USER);

        this.testUser = userRepository.save(testUser);
        this.testUser.setPassword(userPassword);
    }

    @Test
    @Order(1)
    public void testRegisteredUser_whenValidUserDetailsProvided_shouldLoginSuccessfullyAndReceiveJWT() throws Exception {

        LoginRequestDto loginRequestDto = new LoginRequestDto(testUser.getUsername(), this.testUser.getPassword());
        String loginRequestDtoJson = objectMapper.writeValueAsString(loginRequestDto);

        String response = mockMvc.perform(MockMvcRequestBuilders.post(envConfigs.AUTH_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestDtoJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginResponseDto loginResponseDto = objectMapper.readValue(response, LoginResponseDto.class);

        assertThat(loginResponseDto).isNotNull();
        assertThat(loginResponseDto.userId()).isEqualTo(testUser.getId());
        assertThat(loginResponseDto.userName()).isEqualTo(testUser.getUsername());
        assertThat(loginResponseDto.email()).isEqualTo(testUser.getEmail());
        assertThat(loginResponseDto.jwtToken()).matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");

        // store jwt token for later use
        this.testUserJWT = "Bearer " + loginResponseDto.jwtToken();
    }

    @Test
    @Order(2)
    public void testCreateQuiz_whenValidQuizDataProvided_shouldCreateQuizSuccessfully() throws Exception {

        QuizCreateDto quizCreateDto = new QuizCreateDto(testUser.getId(), "My first quiz",
                "https://www.youtube.com/watch?v=IFx8eABfivg",
                Map.of("OPENAI_API_KEY", apiKeyTest),
                UserQuiz.Language.ES,
                UserQuiz.Type.MULTIPLE_CHOICE,
                UserQuiz.Difficulty.HARD);

        String quizCreateDtoJson = objectMapper.writeValueAsString(quizCreateDto);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/quizzes/new")
                        .header("Authorization", testUserJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(quizCreateDtoJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        QuizCreateResultDto quizCreateResultDto = objectMapper.readValue(response, QuizCreateResultDto.class);

        assertThat(quizCreateResultDto).isNotNull();
        assertThat(quizCreateResultDto.userId()).isEqualTo(testUser.getId());
        assertThat(quizCreateResultDto.quizName()).isEqualTo(quizCreateDto.quizName()
                .toLowerCase());
        assertThat(quizCreateResultDto.quizId()).isNotNull();

        // store quiz data info
        this.quizCreateData = quizCreateResultDto;
    }

    @Test
    @Order(3)
    public void testGetQuizByUserQuizId_whenUserQuizIdValid_shouldFetchQuiz() throws Exception {

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/quizzes/{quizId}/users/{userId}", quizCreateData.quizId(), testUser.getId())
                        .header("Authorization", testUserJWT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        QuizOutboundDto quizOutboundDto = objectMapper.readValue(response, QuizOutboundDto.class);

        assertThat(quizOutboundDto).isNotNull();
        assertThat(quizOutboundDto.userId()).isEqualTo(testUser.getId());
        assertThat(quizOutboundDto.quizName()).isEqualTo(quizCreateData.quizName()
                .toLowerCase());
        assertThat(quizOutboundDto.quizId()).isEqualTo(quizCreateData.quizId());

        assertThat(quizOutboundDto.dateCreated()).isEqualTo(LocalDate.now()
                .toString());
        assertThat(quizOutboundDto.numTries()).isEqualTo(0);
        assertThat(quizOutboundDto.numCorrect()).isEqualTo(0);

        assertThat(quizOutboundDto.numQuestions()).isGreaterThan(0);
        assertThat(quizOutboundDto.language()).isEqualTo(UserQuiz.Language.ES);
        assertThat(quizOutboundDto.type()).isEqualTo(UserQuiz.Type.MULTIPLE_CHOICE);
        assertThat(quizOutboundDto.difficulty()).isEqualTo(UserQuiz.Difficulty.HARD);

        var videoMetadata = quizOutboundDto.metadata();
        assertThat(videoMetadata.title()).hasSizeGreaterThan(0);
        assertThat(videoMetadata.videoUrl()).isEqualTo("IFx8eABfivg");
        assertThat(videoMetadata.thumbnailUrl()).isEqualTo("https://i.ytimg.com/vi/IFx8eABfivg/hq720.jpg");
        assertThat(videoMetadata.description()).hasSizeGreaterThan(10);
        assertThat(videoMetadata.viewers()).isGreaterThan(0);
        assertThat(videoMetadata.publishDate()).isInThePast();
        assertThat(videoMetadata.viewers()).isGreaterThan(0);
        assertThat(videoMetadata.author()).isEqualTo("BBC News Mundo");

        // store quiz data
        this.quizOutboundData = quizOutboundDto;
    }

    @Test
    @Order(4)
    public void testCreateQuiz_whenQuizNameAlreadyExists_shouldThrowException() throws Exception {

        QuizCreateDto quizCreateDto = new QuizCreateDto(testUser.getId(), quizOutboundData.quizName(),
                "https://www.youtube.com/watch?v=" + quizOutboundData.metadata()
                        .videoUrl(),
                Map.of("OPENAI_API_KEY", apiKeyTest),
                quizOutboundData.language(),
                quizOutboundData.type(),
                quizOutboundData.difficulty());

        String quizCreateDtoJson = objectMapper.writeValueAsString(quizCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/quizzes/new")
                        .header("Authorization", testUserJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(quizCreateDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(CoreMatchers.containsString(String.format("Quiz name '%s' already exists for user %s.", quizOutboundData.quizName(), quizCreateData.userId()))));
    }

    @Test
    @Order(5)
    public void testCreateQuiz_whenInvalidAPIKeyProvided_shouldThrowException() throws Exception {

        QuizCreateDto quizCreateDto = new QuizCreateDto(testUser.getId(), "My second quiz",
                "https://www.youtube.com/watch?v=" + quizOutboundData.metadata()
                        .videoUrl(),
                Map.of("OPENAI_API_KEY", "invalid api key"),
                quizOutboundData.language(),
                quizOutboundData.type(),
                quizOutboundData.difficulty());

        String quizCreateDtoJson = objectMapper.writeValueAsString(quizCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/quizzes/new")
                        .header("Authorization", testUserJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(quizCreateDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(CoreMatchers.containsString("Invalid API key provided.")));
    }

    @Test
    @Order(6)
    public void testUpdateQuiz_whenValidQuizUpdateDataProvided_shouldUpdateQuizEntity() throws Exception {

        QuizUpdateDto quizUpdateDto = new QuizUpdateDto(testUser.getId(), quizOutboundData.quizId(), quizOutboundData.numQuestions() - 1, quizOutboundData.quizName() + "_updated");
        String quizUpdateDtoJson = objectMapper.writeValueAsString(quizUpdateDto);

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/update")
                        .header("Authorization", testUserJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(quizUpdateDtoJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        QuizOutboundDto quizUpdateResultDto = objectMapper.readValue(response, QuizOutboundDto.class);

        assertThat(quizUpdateResultDto).isNotNull();
        assertThat(quizUpdateResultDto.userId()).isEqualTo(testUser.getId());
        assertThat(quizUpdateResultDto.quizId()).isEqualTo(quizOutboundData.quizId());
        assertThat(quizUpdateResultDto.numCorrect()).isEqualTo(quizUpdateDto.numCorrect());
        assertThat(quizUpdateResultDto.numTries()).isEqualTo(1);
        assertThat(quizUpdateResultDto.quizName()).isEqualTo(quizUpdateDto.quizName()
                .toLowerCase());
    }

    @Test
    @Order(7)
    public void testGetQuizDetails_whenValidUserQuizIdProvided_shouldFetchQuizDetails() throws Exception {

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/quizzes/{quizId}/users/{userId}/details", quizOutboundData.quizId(), testUser.getId())
                        .header("Authorization", testUserJWT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        QuizDetailsOutboundDto quizDetailsDto = objectMapper.readValue(response, QuizDetailsOutboundDto.class);

        assertThat(quizDetailsDto).isNotNull();
        assertThat(quizDetailsDto.userId()).isEqualTo(testUser.getId());
        assertThat(quizDetailsDto.quizId()).isEqualTo(quizOutboundData.quizId());
        assertThat(quizDetailsDto.questionAnswersList()).hasSize(quizOutboundData.numQuestions());

        var quizQuestionFirst = quizDetailsDto.questionAnswersList()
                .getFirst();
        assertThat(quizQuestionFirst.question()).hasSizeGreaterThan(0);
        assertThat(quizQuestionFirst.correctAnswer()).hasSizeGreaterThan(0);
        assertThat(quizQuestionFirst.wrongAnswers()).hasSize(3);
        assertThat(quizQuestionFirst.context()).hasSizeGreaterThan(0);
    }

    @Test
    @Order(8)
    public void testDeleteQuiz_whenValidUserQuizIdProvided_shouldDeleteQuiz() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/quizzes/{quizId}/users/{userId}", quizOutboundData.quizId(), testUser.getId())
                        .header("Authorization", testUserJWT))
                .andExpect(status().isNoContent());

        Page<QuizOutboundDto> allUserQuizzes = quizService.getAllUserQuizzes(testUser.getId(), PageRequest.of(0, 10));

        assertThat(allUserQuizzes.getTotalElements()).isEqualTo(0);
    }

}

