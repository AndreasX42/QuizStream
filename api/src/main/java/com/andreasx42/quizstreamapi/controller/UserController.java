package com.andreasx42.quizstreamapi.controller;

import com.andreasx42.quizstreamapi.dto.user.UserOutboundDto;
import com.andreasx42.quizstreamapi.dto.user.UserRegisterDto;
import com.andreasx42.quizstreamapi.dto.user.UserUpdateDto;
import com.andreasx42.quizstreamapi.exception.ErrorResponse;
import com.andreasx42.quizstreamapi.service.UserService;
import com.andreasx42.quizstreamapi.util.mapper.UserMapper;
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

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Returns a user based on id")
    @ApiResponses(value = {@ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserOutboundDto.class))),})
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<UserOutboundDto> getUserById(@PathVariable Long id) {

        UserOutboundDto userDto = userMapper.mapFromEntityOutbound(userService.getById(id));
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Operation(summary = "Retrieves paged list of users")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all users", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserOutboundDto.class))))
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UserOutboundDto>> getAllUsers(Pageable pageable) {

        Page<UserOutboundDto> users = userService.getAll(pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Creates a user from provided data")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful creation of user", content = @Content(schema = @Schema(implementation = UserOutboundDto.class))),
            @ApiResponse(responseCode = "400", description = "Creation of user not successful", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserOutboundDto> registerUser(@Valid @RequestBody UserRegisterDto userDto) {

        UserOutboundDto userRegisteredDto = userService.create(userDto);
        return new ResponseEntity<>(userRegisteredDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Updates a user from provided data")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful update of user", content = @Content(schema = @Schema(implementation = UserOutboundDto.class))),
            @ApiResponse(responseCode = "400", description = "Unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<UserOutboundDto> updateUser(@Valid @RequestBody UserUpdateDto userDto, @PathVariable Long id) {

        UserOutboundDto userUpdatedDto = userService.update(id, userDto);
        return new ResponseEntity<>(userUpdatedDto, HttpStatus.OK);
    }

    @Operation(summary = "Deletes user with given ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful deletion of user", content = @Content(schema = @Schema(implementation = HttpStatus.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {

        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
