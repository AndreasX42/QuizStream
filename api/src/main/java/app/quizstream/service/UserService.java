package app.quizstream.service;

import app.quizstream.dto.auth.LoginRequestDto;
import app.quizstream.dto.auth.LoginResponseDto;
import app.quizstream.dto.user.UserOutboundDto;
import app.quizstream.dto.user.UserRegisterDto;
import app.quizstream.dto.user.UserUpdateRequestDto;
import app.quizstream.dto.user.UserUpdateResponseDto;
import app.quizstream.entity.User;
import app.quizstream.exception.DuplicateEntityException;
import app.quizstream.exception.EntityNotFoundException;
import app.quizstream.repository.UserRepository;
import app.quizstream.security.config.EnvConfigs;
import app.quizstream.util.mapper.UserMapper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;
    private final EnvConfigs envConfigs;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
            UserMapper userMapper, EnvConfigs envConfigs) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
        this.envConfigs = envConfigs;
    }

    public User getByUserName(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new EntityNotFoundException(username, User.class));
    }

    public User getById(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElseThrow(() -> new EntityNotFoundException(id.toString(), User.class));
    }

    public Page<UserOutboundDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::mapFromEntityOutbound);
    }

    public UserOutboundDto create(UserRegisterDto userDto) {
        if (userRepository.findByEmail(userDto.email())
                .isPresent()) {
            throw new DuplicateEntityException("email", userDto.email(), User.class);
        }

        if (userRepository.findByUsername(userDto.username())
                .isPresent()) {
            throw new DuplicateEntityException("username", userDto.username(), User.class);
        }

        User user = userMapper.mapToEntity(userDto);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return userMapper.mapFromEntityOutbound(userRepository.save(user));
    }

    public void delete(UUID id) {
        if (userRepository.findById(id)
                .isEmpty()) {
            throw new EntityNotFoundException(id.toString(), User.class);
        }

        userRepository.deleteById(id);
    }

    public UserUpdateResponseDto update(UUID id, UserUpdateRequestDto userDto) {

        User user = getById(id);
        String refreshedJwtToken = null;

        // Check if email needs to be updated
        if (Objects.nonNull((userDto.email())) && !user.getEmail()
                .equals(userDto.email())) {

            boolean newEmailIsUnique = userRepository.findByEmail(userDto.email())
                    .isEmpty();

            if (newEmailIsUnique) {
                user.setEmail(userDto.email());
            } else {
                throw new DuplicateEntityException("email", userDto.email(), User.class);
            }
        }

        // Check if username needs to be updated
        if (Objects.nonNull((userDto.username())) && !userDto.username()
                .equals(user.getUsername())) {

            boolean newUsernameIsUnique = userRepository.findByUsername(userDto.username())
                    .isEmpty();

            if (newUsernameIsUnique) {
                user.setUsername(userDto.username());

                refreshedJwtToken = JWT.create()
                        .withSubject(userDto.username())
                        .withExpiresAt(new Date(System.currentTimeMillis() + envConfigs.TOKEN_EXPIRATION))
                        .withClaim("role", user.getRole()
                                .toString())
                        .sign(Algorithm.HMAC512(envConfigs.getJwtSecret()));

            } else {
                throw new DuplicateEntityException("username", userDto.username(), User.class);
            }
        }

        if (Objects.nonNull(userDto.password())) {
            user.setPassword(bCryptPasswordEncoder.encode(userDto.password()));
        }

        User updatedUser = userRepository.save(user);

        return new UserUpdateResponseDto(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail(),
                refreshedJwtToken);
    }

    public LoginResponseDto authenticateUser(LoginRequestDto userCredentials) {

        User user = getByUserName(userCredentials.username());

        if (!bCryptPasswordEncoder.matches(userCredentials.password(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        return new LoginResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
                        .name());

    }

    public String getJwtToken(LoginResponseDto loginResponseDto) {
        return JWT.create()
                .withSubject(loginResponseDto.userName())
                .withExpiresAt(new Date(System.currentTimeMillis() + envConfigs.TOKEN_EXPIRATION))
                .withClaim("role", loginResponseDto.role())
                .sign(Algorithm.HMAC512(envConfigs.getJwtSecret()));
    }
}
