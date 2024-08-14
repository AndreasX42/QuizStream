package com.andreasx42.quizstreamapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dto for login of user.")
public record UserLoginDto(

        @NotNull @Email String email,
        @NotNull String password) {

}
