package com.andreasx42.quizstreamapi.controller;

import com.andreasx42.quizstreamapi.dto.auth.LoginRequestDto;
import com.andreasx42.quizstreamapi.dto.auth.LoginResponseDto;
import com.andreasx42.quizstreamapi.dto.user.UserOutboundDto;
import com.andreasx42.quizstreamapi.dto.user.UserRegisterDto;
import com.andreasx42.quizstreamapi.dto.user.UserUpdateRequestDto;
import com.andreasx42.quizstreamapi.dto.user.UserUpdateResponseDto;
import com.andreasx42.quizstreamapi.exception.ErrorResponse;
import com.andreasx42.quizstreamapi.service.UserService;
import com.andreasx42.quizstreamapi.util.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
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

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Authenticates user")
    @ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Authentication failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "200", description = "Authentication succeeded  ", content = @Content(schema = @Schema(implementation = LoginRequestDto.class))),})
    @PostMapping(value = "/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDto> authenticateUser(@RequestBody LoginRequestDto userCredentials) {

        // authenticate user and generate jwt
        LoginResponseDto loginResponseDto = userService.authenticateUser(userCredentials);
        String token = userService.getJwtToken(loginResponseDto);

        // create headers and add the Authorization header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        // return the response with the login data and the header
        return new ResponseEntity<>(loginResponseDto, headers, HttpStatus.OK);
    }

    @Operation(summary = "Returns a user based on id")
    @ApiResponses(value = {@ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserOutboundDto.class))),})
    @GetMapping(value = "id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<UserOutboundDto> getUserById(@PathVariable Long id) {

        UserOutboundDto userDto = userMapper.mapFromEntityOutbound(userService.getById(id));
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Operation(summary = "Returns a user based on username")
    @ApiResponses(value = {@ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserOutboundDto.class))),})
    @GetMapping(value = "name/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userName == principal.username or hasAuthority('ADMIN')")
    public ResponseEntity<UserOutboundDto> getUserByUserName(@PathVariable String userName) {

        UserOutboundDto userDto = userMapper.mapFromEntityOutbound(userService.getByUserName(userName));
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Operation(summary = "Creates a user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful creation of user", content = @Content(schema = @Schema(implementation = UserOutboundDto.class))),
            @ApiResponse(responseCode = "400", description = "Creation of user not successful", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserOutboundDto> registerUser(@Valid @RequestBody UserRegisterDto userDto) {

        UserOutboundDto userRegisteredDto = userService.create(userDto);
        return new ResponseEntity<>(userRegisteredDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Updates a user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful update of user", content = @Content(schema = @Schema(implementation = UserOutboundDto.class))),
            @ApiResponse(responseCode = "400", description = "Unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<UserUpdateResponseDto> updateUser(@Valid @RequestBody UserUpdateRequestDto userDto, @PathVariable Long id) {

        UserUpdateResponseDto userUpdatedDto = userService.update(id, userDto);
        return new ResponseEntity<>(userUpdatedDto, HttpStatus.OK);
    }

    @Operation(summary = "Deletes user with given id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful deletion of user", content = @Content(schema = @Schema(implementation = HttpStatus.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {

        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
