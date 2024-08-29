package com.andreasx42.quizstreamapi.service;

import com.andreasx42.quizstreamapi.dto.user.UserRegisterDto;
import com.andreasx42.quizstreamapi.dto.user.UserUpdateDto;
import com.andreasx42.quizstreamapi.entity.User;
import com.andreasx42.quizstreamapi.exception.DuplicateEntityException;
import com.andreasx42.quizstreamapi.repository.UserRepository;
import com.andreasx42.quizstreamapi.util.mapper.UserMapper;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {

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

        Long existingUserId = 1L;
        User existingUser = new User("testUser", "test@user.com", "password");
        UserUpdateDto updateExistingUserDto = new UserUpdateDto(null, "newMail@user.com", null);
        User otherUserWithSameNewEmail = new User("otherUser", "newMail@user.com", "otherPassword");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(updateExistingUserDto.email())).thenReturn(Optional.of(otherUserWithSameNewEmail));

        assertThatThrownBy(() -> userService.update(existingUserId, updateExistingUserDto)).isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining("The user with email '" + updateExistingUserDto.email() + "' does already exist.");
    }

    @Test
    public void testUpdateUser_whenExistingUsernameProvided_shouldThrowDuplicateEntityException() {

        Long existingUserId = 1L;
        User existingUser = new User("testUser", "test@user.com", "password");
        UserUpdateDto updateExistingUserDto = new UserUpdateDto("newTestUserName", null, null);
        User otherUserWithSameNewUsername = new User("newTestUserName", "newTestUserName@user.com", "otherPassword");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(updateExistingUserDto.username())).thenReturn(Optional.of(otherUserWithSameNewUsername));

        assertThatThrownBy(() -> userService.update(existingUserId, updateExistingUserDto)).isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining("The user with username '" + updateExistingUserDto.username() + "' does already exist.");
    }

    @Test
    public void testUpdateUser_whenValidUpdateDataProvided_shouldSaveUpdatedEntity() {

        Long existingUserId = 1L;
        User existingUser = new User("testUser", "test@user.com", "password");
        UserUpdateDto updateExistingUserDto = new UserUpdateDto("newTestUserName", "newTestUserMail@user.com", "newPassword");

        when(userRepository.findById(existingUserId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(updateExistingUserDto.email())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(updateExistingUserDto.username())).thenReturn(Optional.empty());
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        userService.update(existingUserId, updateExistingUserDto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo(updateExistingUserDto.username());
        assertThat(savedUser.getEmail()).isEqualTo(updateExistingUserDto.email());
        assertThat(bCryptPasswordEncoder.matches(updateExistingUserDto.password(), savedUser.getPassword())).isTrue();
    }
}
