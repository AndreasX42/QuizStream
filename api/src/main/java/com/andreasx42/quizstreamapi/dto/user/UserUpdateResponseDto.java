package com.andreasx42.quizstreamapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dto for update of user.")
public record UserUpdateResponseDto(

        @NotNull Long id,
        @NotNull String username,
        @NotNull @Email String email,
        @Nullable String refreshedJwtToken) {

}
