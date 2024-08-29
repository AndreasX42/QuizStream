package com.andreasx42.quizstreamapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dto for registration of user.")
public record UserRegisterDto(

        @NotNull String username,
        @NotNull String password,
        @NotNull @Email String email) {

}
