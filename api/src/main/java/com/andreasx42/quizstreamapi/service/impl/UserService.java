package com.andreasx42.quizstreamapi.service.impl;

import com.andreasx42.quizstreamapi.dto.UserDto;
import com.andreasx42.quizstreamapi.entity.User;
import com.andreasx42.quizstreamapi.exception.DuplicateEntityException;
import com.andreasx42.quizstreamapi.exception.EntityNotFoundException;
import com.andreasx42.quizstreamapi.repository.UserRepository;
import com.andreasx42.quizstreamapi.service.api.IUserService;
import com.andreasx42.quizstreamapi.service.mapper.impl.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserMapper userMapper;

	@Override
	public User getByName(String username) {
		Optional<User> userOptional = userRepository.findByUsername(username);
		return userOptional.orElseThrow(() -> new EntityNotFoundException(username, User.class));
	}

	@Override
	public User getById(Long id) {
		Optional<User> userOptional = userRepository.findById(id);
		return userOptional.orElseThrow(() -> new EntityNotFoundException(id, User.class));
	}

	@Override
	public Page<UserDto> getAll(Pageable pageable) {
		return userRepository.findAll(pageable)
		                     .map(userMapper::mapFromEntity);
	}

	@Override
	public UserDto create(UserDto userDto) {
		if(userRepository.findByEmail(userDto.email())
		                 .isPresent()) {
			throw new DuplicateEntityException("email", userDto.email(), User.class);
		}

		if(userRepository.findByUsername(userDto.username())
		                 .isPresent()) {
			throw new DuplicateEntityException("username", userDto.username(), User.class);
		}

		User user = userMapper.mapToEntity(userDto);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		return userMapper.mapFromEntity(userRepository.save(user));
	}

	@Override
	public void delete(Long id) {
		if(userRepository.findById(id)
		                 .isEmpty()) {
			throw new EntityNotFoundException(id, User.class);
		}

		userRepository.deleteById(id);
	}

	@Override
	public UserDto update(Long id, UserDto userDto) {

		User user = getById(id);

		if(!userDto.email()
		           .equals(user.getEmail()) && userRepository.findByEmail(userDto.email())
		                                                     .isPresent()) {
			throw new DuplicateEntityException("email", userDto.email(), User.class);
		} else {
			user.setEmail(userDto.email());
		}

		if(!userDto.username()
		           .equals(user.getUsername()) && userRepository.findByUsername(userDto.username())
		                                                        .isPresent()) {
			throw new DuplicateEntityException("username", userDto.username(), User.class);
		} else {
			user.setUsername(userDto.username());
		}

		if(userDto.password() != null) {
			user.setPassword(bCryptPasswordEncoder.encode(userDto.password()));
		}

		return userMapper.mapFromEntity(userRepository.save(user));
	}

}
