package app.quizstream.controller;

import app.quizstream.dto.auth.LoginRequestDto;
import app.quizstream.dto.auth.LoginResponseDto;
import app.quizstream.dto.user.UserOutboundDto;
import app.quizstream.dto.user.UserRegisterDto;
import app.quizstream.dto.user.UserUpdateRequestDto;
import app.quizstream.dto.user.UserUpdateResponseDto;
import app.quizstream.entity.User;
import app.quizstream.entity.UserQuiz;
import app.quizstream.entity.UserQuizId;
import app.quizstream.entity.embedding.LangchainPGEmbedding;
import app.quizstream.entity.request.QuizRequestId;
import app.quizstream.repository.*;
import app.quizstream.security.config.EnvConfigs;
import app.quizstream.service.UserService;
import app.quizstream.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.escoffier.loom.loomunit.LoomUnitExtension;
import me.escoffier.loom.loomunit.ShouldNotPin;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Commit
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(LoomUnitExtension.class)
@ShouldNotPin
public class UserControllerIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserQuizRepository userQuizRepository;
    private final QuizRequestRepository quizRequestRepository;
    private final LangchainPGCollectionRepository langchainPGCollectionRepository;
    private final LangchainPGEmbeddingRepository langchainPGEmbeddingRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EnvConfigs envConfigs;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;


    @Autowired
    public UserControllerIntegrationTest(UserService userService, UserRepository userRepository, UserQuizRepository userQuizRepository, QuizRequestRepository quizRequestRepository, LangchainPGCollectionRepository langchainPGCollectionRepository, LangchainPGEmbeddingRepository langchainPGEmbeddingRepository, BCryptPasswordEncoder bCryptPasswordEncoder, EnvConfigs envConfigs, ObjectMapper objectMapper, MockMvc mockMvc) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userQuizRepository = userQuizRepository;
        this.quizRequestRepository = quizRequestRepository;
        this.langchainPGCollectionRepository = langchainPGCollectionRepository;
        this.langchainPGEmbeddingRepository = langchainPGEmbeddingRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.envConfigs = envConfigs;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

    private User testUser;
    private User testAdmin;
    private String testUserJWT;
    private String testAdminJWT;

    @BeforeAll
    public void setUp() {
        setUpUserAccount();
        setUpAdminAccount();
    }

    private void setUpUserAccount() {
        String userName = "John_Doe_1";

        testUser = new User();
        testUser.setUsername(userName);
        testUser.setEmail(userName + "@mail.com");
        testUser.setPassword(userName + "_password");
    }

    private void setUpAdminAccount() {
        String adminName = "System_Admin";
        String adminPassword = adminName + "_password";
        this.testAdmin = new User();
        this.testAdmin.setUsername(adminName);
        this.testAdmin.setEmail(adminName + "@mail.com");
        this.testAdmin.setPassword(bCryptPasswordEncoder.encode(adminPassword));
        this.testAdmin.setRole(User.Role.ADMIN);

        this.testAdmin = userRepository.save(testAdmin);
        this.testAdmin.setPassword(adminPassword);
    }

    @Test
    @Order(1)
    public void testRegisterUser_whenValidUserDetailsProvided_shouldCreateUserAndReturnUserInformation() throws Exception {

        UserRegisterDto registerUserDto = new UserRegisterDto(testUser.getUsername(), testUser.getPassword(), testUser.getEmail());
        String registerUserDtoJson = objectMapper.writeValueAsString(registerUserDto);

        String response = mockMvc.perform(MockMvcRequestBuilders.post(envConfigs.REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerUserDtoJson))
                .andExpect(status()
                        .isCreated())
                .andExpect(jsonPath("$.password")
                        .doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserOutboundDto createdUserDto = objectMapper.readValue(response, UserOutboundDto.class);

        assertThat(createdUserDto).isNotNull();
        assertThat(createdUserDto.username()).isEqualTo(registerUserDto.username());
        assertThat(createdUserDto.email()).isEqualTo(registerUserDto.email());

        // store for next tests
        this.testUser.setId(createdUserDto.id());
    }

    @Test
    @Order(2)
    public void testAuthenticateUser_whenCorrectUserCredentialsProvided_shouldAuthenticateAndReturnJWT() throws Exception {

        LoginRequestDto loginRequestDto = new LoginRequestDto(testUser.getUsername(), this.testUser.getPassword());
        String loginRequestDtoJson = objectMapper.writeValueAsString(loginRequestDto);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(envConfigs.AUTH_PATH_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestDtoJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // check that jwt token is in header
        Util.assertThatIsValidJwtToken(response, testUser.getUsername(), testUser.getRole()
                        .name(),
                envConfigs.BEARER_PREFIX, envConfigs.getJwtSecret(), envConfigs.TOKEN_EXPIRATION);

        // test endpoint response
        LoginResponseDto loginResponseDto = objectMapper.readValue(response.getContentAsString(), LoginResponseDto.class);

        assertThat(loginResponseDto).isNotNull();
        assertThat(loginResponseDto.userId()).isEqualTo(testUser.getId());
        assertThat(loginResponseDto.userName()).isEqualTo(testUser.getUsername());
        assertThat(loginResponseDto.email()).isEqualTo(testUser.getEmail());
        assertThat(loginResponseDto.role()).isEqualTo(testUser.getRole()
                .name());

        // store jwt token for later use
        this.testUserJWT = response.getHeader("Authorization");
    }

    @Test
    @Order(3)
    public void testGetUserById_whenValidUserIdProvided_shouldFetchCorrespondingUserFromDb() throws Exception {

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/users/id/{id}", testUser.getId())
                        .header("Authorization", testUserJWT))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserOutboundDto fetchedUserDto = objectMapper.readValue(response, UserOutboundDto.class);

        assertThat(fetchedUserDto.id()).isEqualTo(testUser.getId());
        assertThat(fetchedUserDto.username()).isEqualTo(testUser.getUsername());
        assertThat(fetchedUserDto.email()).isEqualTo(testUser.getEmail());
    }

    @Test
    @Order(4)
    public void testGetUserByUsername_whenValidUsernameProvided_shouldFetchCorrespondingUserFromDb() throws Exception {

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/users/name/{name}", testUser.getUsername())
                        .header("Authorization", testUserJWT))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserOutboundDto fetchedUserDto = objectMapper.readValue(response, UserOutboundDto.class);

        assertThat(fetchedUserDto.id()).isEqualTo(testUser.getId());
        assertThat(fetchedUserDto.username()).isEqualTo(testUser.getUsername());
        assertThat(fetchedUserDto.email()).isEqualTo(testUser.getEmail());
    }

    @Test
    @Order(5)
    public void testAuthenticateAdmin_whenUserProvidesValidAdminCredentials_shouldReceiveCorrectlyFormatedJWT() throws Exception {

        // first get jwt for admin
        LoginRequestDto loginRequestDto = new LoginRequestDto(testAdmin.getUsername(), this.testAdmin.getPassword());
        String loginRequestDtoJson = objectMapper.writeValueAsString(loginRequestDto);

        // test second auth endpoint '/users/authenticate'
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(envConfigs.AUTH_PATH_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestDtoJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // check that jwt token is in header
        Util.assertThatIsValidJwtToken(response, testAdmin.getUsername(), testAdmin.getRole()
                        .name(),
                envConfigs.BEARER_PREFIX, envConfigs.getJwtSecret(), envConfigs.TOKEN_EXPIRATION);

        // test endpoint response
        LoginResponseDto loginResponseDto = objectMapper.readValue(response.getContentAsString(), LoginResponseDto.class);

        assertThat(loginResponseDto).isNotNull();
        assertThat(loginResponseDto.userId()).isEqualTo(testAdmin.getId());
        assertThat(loginResponseDto.userName()).isEqualTo(testAdmin.getUsername());
        assertThat(loginResponseDto.email()).isEqualTo(testAdmin.getEmail());
        assertThat(loginResponseDto.role()).isEqualTo(testAdmin.getRole()
                .name());

        this.testAdminJWT = response.getHeader("Authorization");
    }

    @Test
    @Order(6)
    public void testUpdateUser_whenProvidedValidNewUserDetails_shouldUpdateUserInDb() throws Exception {

        UserUpdateRequestDto newUserDataDto = new UserUpdateRequestDto("new_" + testUser.getUsername(), "new_" + testUser.getEmail(), "new_" + testUser.getPassword());
        String newUserDataDtoJson = objectMapper.writeValueAsString(newUserDataDto);

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", testUser.getId())
                        .header("Authorization", testUserJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserDataDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password")
                        .doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserUpdateResponseDto updatedUserDto = objectMapper.readValue(response, UserUpdateResponseDto.class);
        String newHashedPassword = userService.getByUserName(updatedUserDto.username())
                .getPassword();

        assertThat(updatedUserDto.id()).isEqualTo(testUser.getId());
        assertThat(updatedUserDto.username()).isEqualTo(newUserDataDto.username());
        assertThat(updatedUserDto.email()).isEqualTo(newUserDataDto.email());
        assertThat(bCryptPasswordEncoder.matches(newUserDataDto.password(), newHashedPassword)).isTrue();

        // update details in testUser object
        testUser.setUsername(updatedUserDto.username());
        testUser.setEmail(updatedUserDto.email());
        testUser.setPassword(newHashedPassword);
        testUserJWT = "Bearer " + updatedUserDto.refreshedJwtToken();
    }

    @Test
    @Order(7)
    public void testUpdateUser_whenProvidedNoNewUserDetails_shouldMakeNoChangesToEntity() throws Exception {

        UserUpdateRequestDto newUserDataDto = new UserUpdateRequestDto(null, null, null);
        String newUserDataDtoJson = objectMapper.writeValueAsString(newUserDataDto);

        String response = mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", testUser.getId())
                        .header("Authorization", testUserJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserDataDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password")
                        .doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserOutboundDto updatedUserDto = objectMapper.readValue(response, UserOutboundDto.class);
        String newHashedPassword = userService.getByUserName(updatedUserDto.username())
                .getPassword();

        assertThat(updatedUserDto.id()).isEqualTo(testUser.getId());
        assertThat(updatedUserDto.username()).isEqualTo(testUser.getUsername());
        assertThat(updatedUserDto.email()).isEqualTo(testUser.getEmail());
        assertThat(newHashedPassword).isEqualTo(testUser.getPassword());
    }

    @Test
    @Order(8)
    public void testUpdateUser_whenUserUpdatesAdminDetails_shouldRejectRequest() throws Exception {

        UserUpdateRequestDto newUserDataDto = new UserUpdateRequestDto(null, null, null);
        String newUserDataDtoJson = objectMapper.writeValueAsString(newUserDataDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", testAdmin.getId())
                        .header("Authorization", testUserJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserDataDtoJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(9)
    public void testUpdateUser_whenAdminUpdatesUsersDetails_shouldPermitRequest() throws Exception {

        UserUpdateRequestDto newUserDataDto = new UserUpdateRequestDto(null, null, null);
        String newUserDataDtoJson = objectMapper.writeValueAsString(newUserDataDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", testUser.getId())
                        .header("Authorization", testAdminJWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserDataDtoJson))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    public void testDeleteUser_whenUserTriesToDeleteAdminAccount_shouldBeDenied() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", testAdmin.getId())
                        .header("Authorization", testUserJWT))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(11)
    public void testDeleteUser_whenValidUserIdProvided_shouldDeleteEntity() throws Exception {

        // get user info about quizzes created
        List<UUID> quizIds = userQuizRepository.findAll()
                .stream()
                .map(UserQuiz::getId)
                .filter(id -> id
                        .getUserId()
                        .equals(testUser.getId()))
                .map(UserQuizId::getQuizId)
                .toList();

        // delete user
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", testUser.getId())
                        .header("Authorization", testUserJWT))
                .andExpect(status().isNoContent());

        // check that all user rows from all tables have been deleted
        mockMvc.perform(MockMvcRequestBuilders.get("/users/id/{id}", testUser.getId())
                        .header("Authorization", testUserJWT))
                .andExpect(status().isNotFound());

        assertThat(userQuizRepository.findByUser_Id(testUser.getId(), Pageable.ofSize(1))
                .getTotalElements()).isEqualTo(0);

        assertThat(quizRequestRepository.findAllById(quizIds.stream()
                .map(quiz_id -> new QuizRequestId(testUser.getId(), quiz_id.toString()))
                .toList())).hasSize(0);

        assertThat(langchainPGCollectionRepository.findAllById(quizIds)).hasSize(0);

        assertThat(langchainPGEmbeddingRepository.findAll()
                .stream()
                .map(LangchainPGEmbedding::getCollectionId)
                .filter(quizIds::contains)).hasSize(0);

    }

}

