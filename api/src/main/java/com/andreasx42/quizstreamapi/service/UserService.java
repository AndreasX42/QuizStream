package com.andreasx42.quizstreamapi.service;

import com.andreasx42.quizstreamapi.dto.user.UserOutboundDto;
import com.andreasx42.quizstreamapi.dto.user.UserRegisterDto;
import com.andreasx42.quizstreamapi.dto.user.UserUpdateDto;
import com.andreasx42.quizstreamapi.entity.User;
import com.andreasx42.quizstreamapi.exception.DuplicateEntityException;
import com.andreasx42.quizstreamapi.exception.EntityNotFoundException;
import com.andreasx42.quizstreamapi.repository.UserRepository;
import com.andreasx42.quizstreamapi.util.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;

    public User getByUserName(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new EntityNotFoundException(username, User.class));
    }

    public User getById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElseThrow(() -> new EntityNotFoundException(id, User.class));
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

    public void delete(Long id) {
        if (userRepository.findById(id)
                .isEmpty()) {
            throw new EntityNotFoundException(id, User.class);
        }

        userRepository.deleteById(id);
    }

    public UserOutboundDto update(Long id, UserUpdateDto userDto) {

        User user = getById(id);

        // Check if email needs to be updated
        if (Objects.nonNull((userDto.email()))) {
            boolean updatesEmail = !userDto.email()
                    .equals(user.getEmail());
            boolean newEmailIsUnique = userRepository.findByEmail(userDto.email())
                    .isEmpty();

            if (updatesEmail && newEmailIsUnique) {
                user.setEmail(userDto.email());
            }

            if (updatesEmail && !newEmailIsUnique) {
                throw new DuplicateEntityException("email", userDto.email(), User.class);
            }
        }

        // Check if username needs to be updated
        if (Objects.nonNull((userDto.username()))) {
            boolean updatesUsername = !userDto.username()
                    .equals(user.getUsername());
            boolean newUsernameIsUnique = userRepository.findByUsername(userDto.username())
                    .isEmpty();

            if (updatesUsername && newUsernameIsUnique) {
                user.setUsername(userDto.username());
            }

            if (updatesUsername && !newUsernameIsUnique) {
                throw new DuplicateEntityException("username", userDto.username(), User.class);
            }
        }

        if (Objects.nonNull(userDto.password())) {
            user.setPassword(bCryptPasswordEncoder.encode(userDto.password()));
        }

        return userMapper.mapFromEntityOutbound(userRepository.save(user));
    }

}
