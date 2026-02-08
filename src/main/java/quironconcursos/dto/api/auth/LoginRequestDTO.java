package quironconcursos.dto.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record LoginRequestDTO(
        @Email
        @Size(min = 5, max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 50)
        String password
) implements Serializable {}
