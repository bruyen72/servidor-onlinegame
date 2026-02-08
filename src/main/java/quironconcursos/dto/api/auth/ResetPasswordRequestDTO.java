package quironconcursos.dto.api.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ResetPasswordRequestDTO(
        @NotBlank
        String resetPasswordToken,

        @NotBlank
        @Size(min = 8, max = 50)
        String newPassword
) implements Serializable {}
