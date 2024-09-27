package app.quizstream.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Dto for outbound operations.")
public record UserOutboundDto(

        @NotNull UUID id,
        @NotNull String username,
        @NotNull @Email String email,
        @NotNull String role) {

}
