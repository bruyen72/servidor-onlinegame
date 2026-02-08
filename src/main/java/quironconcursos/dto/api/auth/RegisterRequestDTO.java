package quironconcursos.dto.api.auth;

import jakarta.validation.constraints.*;

import java.io.Serializable;

public record RegisterRequestDTO(
        @NotBlank
        @Pattern(regexp = "^[a-z0-9_]*$", message = "Username must be lowercase letters, numbers, and underscores only")
        @Size(min = 3, max = 20)
        String username,

        @Email
        @Size(min = 5, max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 50)
        String password,

        @NotNull
        Boolean receiveEmails
) implements Serializable {}
