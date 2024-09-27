package app.quizstream.service;

import app.quizstream.dto.user.UserRegisterDto;
import app.quizstream.dto.user.UserUpdateRequestDto;
import app.quizstream.entity.User;
import app.quizstream.exception.DuplicateEntityException;
import app.quizstream.repository.UserRepository;
import app.quizstream.security.config.EnvConfigs;
import app.quizstream.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EnvConfigs envConfigs;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UUID existingUserId;
    private User existingUser;

    @BeforeEach
    public void setUp() {
        this.existingUserId = UUID.randomUUID();
        this.existingUser = new User("testUser", "test@user.com", "password");

    }

    @Test
    public void testCreateUser_whenExistingEmailProvided_shouldThrowDuplicateEntityException() {

        UserRegisterDto userDto = new UserRegisterDto("testUser", "test@user.com", "password");
        User user = userMapper.mapToEntity(userDto);

        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.create(userDto)).isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining("The user with email '" + userDto.email() + "' does already exist.");

    }

    @Test
    public void testCreateUser_whenExistingUsernameProvided_shouldThrowDuplicateEntityException() {

        UserRegisterDto userDto = new UserRegisterDto("testUser", "test@user.com", "password");
        User user = userMapper.mapToEntity(userDto);

        when(userRepository.findByUsername((userDto.username()))).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.create(userDto)).isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining("The user with username '" + userDto.username() + "' does already exist.");
    }

    @Test
    public void testUpdateUser_whenExistingEmailProvided_shouldThrowDuplicateEntityException() {

        UserUpdateRequestDto updateExistingUserDto = new UserUpdateRequestDto(null, "newMail@user.com", null);
        User otherUserWithSameNewEmail = new User("otherUser", "newMail@user.com", "otherPassword");

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(updateExistingUserDto.email()))
                .thenReturn(Optional.of(otherUserWithSameNewEmail));

        assertThatThrownBy(() -> userService.update(existingUserId, updateExistingUserDto))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining(
                        "The user with email '" + updateExistingUserDto.email() + "' does already exist.");
    }

    @Test
    public void testUpdateUser_whenExistingUsernameProvided_shouldThrowDuplicateEntityException() {

        UserUpdateRequestDto updateExistingUserDto = new UserUpdateRequestDto("newTestUserName", null, null);
        User otherUserWithSameNewUsername = new User("newTestUserName", "newTestUserName@user.com", "otherPassword");

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(updateExistingUserDto.username()))
                .thenReturn(Optional.of(otherUserWithSameNewUsername));

        assertThatThrownBy(() -> userService.update(existingUserId, updateExistingUserDto))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining(
                        "The user with username '" + updateExistingUserDto.username() + "' does already exist.");
    }

    @Test
    public void testUpdateUser_whenValidUpdateDataProvided_shouldSaveUpdatedEntity() {

        UserUpdateRequestDto updateExistingUserDto = new UserUpdateRequestDto("newTestUserName",
                "newTestUserMail@user.com", "newPassword");

        when(userRepository.findById(existingUserId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(updateExistingUserDto.email())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(updateExistingUserDto.username())).thenReturn(Optional.empty());
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(envConfigs.getJwtSecret()).thenReturn("JWT_TOKEN");

        userService.update(existingUserId, updateExistingUserDto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo(updateExistingUserDto.username());
        assertThat(savedUser.getEmail()).isEqualTo(updateExistingUserDto.email());
        assertThat(bCryptPasswordEncoder.matches(updateExistingUserDto.password(), savedUser.getPassword())).isTrue();
    }
}
