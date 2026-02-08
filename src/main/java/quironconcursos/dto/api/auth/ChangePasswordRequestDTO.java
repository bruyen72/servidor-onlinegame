package quironconcursos.dto.api.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ChangePasswordRequestDTO(
        @NotBlank
        @Size(min = 8, max = 50)
        String currentPassword,

        @NotBlank
        @Size(min = 8, max = 50)
        String newPassword
) implements Serializable {}
