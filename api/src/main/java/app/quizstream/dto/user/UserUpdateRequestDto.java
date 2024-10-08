package app.quizstream.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;

@Schema(description = "Dto for update of user.")
public record UserUpdateRequestDto(

        @Nullable String username,
        @Nullable @Email String email,
        @Nullable String password) {

}
