package com.andreasx42.quizstreamapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dto for outbound operations.")
public record UserOutboundDto(

        @NotNull Long id,
        @NotNull String username,
        @NotNull @Email String email) {

}
