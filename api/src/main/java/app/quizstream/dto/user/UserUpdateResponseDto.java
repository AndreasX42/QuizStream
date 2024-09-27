package app.quizstream.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Dto for update of user.")
public record UserUpdateResponseDto(

        @NotNull UUID id,
        @NotNull String username,
        @NotNull @Email String email,
        @Nullable String refreshedJwtToken) {

}
