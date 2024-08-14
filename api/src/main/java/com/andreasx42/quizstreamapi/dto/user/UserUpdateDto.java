package com.andreasx42.quizstreamapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;

@Schema(description = "Dto for update of user.")
public record UserUpdateDto(

        @Nullable String username,
        @Nullable @Email String email,
        @Nullable String password) {

}
