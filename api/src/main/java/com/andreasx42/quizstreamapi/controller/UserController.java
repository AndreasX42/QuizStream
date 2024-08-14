package com.andreasx42.quizstreamapi.controller;

import com.andreasx42.quizstreamapi.dto.UserDto;
import com.andreasx42.quizstreamapi.exception.ErrorResponse;
import com.andreasx42.quizstreamapi.service.api.IUserService;
import com.andreasx42.quizstreamapi.service.mapper.impl.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "User Controller", description = "Endpoints to create and manage users")
public class UserController {

	private final IUserService userService;
	private final UserMapper userMapper;

	@Operation(summary = "Returns a user based on an ID")
	@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "User doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "200", description = "Successful retrieval of user", content = @Content(schema = @Schema(implementation = UserDto.class))),})
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {

		UserDto userDto = userMapper.mapFromEntity(userService.getById(id));
		return new ResponseEntity<>(userDto, HttpStatus.OK);
	}

	@Operation(summary = "Retrieves paged list of users")
	@ApiResponse(responseCode = "200", description = "Successful retrieval of all users", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
	@GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<UserDto>> getAllTodos(Pageable pageable) {

		Page<UserDto> todos = userService.getAll(pageable);
		return new ResponseEntity<>(todos, HttpStatus.OK);
	}

	@Operation(summary = "Creates a user from provided payload")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful creation of user", content = @Content(schema = @Schema(implementation = UserDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
	@PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {

		userDto = userService.create(userDto);
		return new ResponseEntity<>(userDto, HttpStatus.CREATED);
	}

	@Operation(summary = "Updates a user from provided payload")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful update of user", content = @Content(schema = @Schema(implementation = UserDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
	public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id) {

		userDto = userService.update(id, userDto);
		return new ResponseEntity<>(userDto, HttpStatus.OK);
	}

	@Operation(summary = "Deletes user with given ID")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful deletion of user", content = @Content(schema = @Schema(implementation = HttpStatus.class))),
			@ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
	public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {

		userService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
