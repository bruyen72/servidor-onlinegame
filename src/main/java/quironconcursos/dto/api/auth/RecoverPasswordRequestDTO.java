package quironconcursos.dto.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record RecoverPasswordRequestDTO(
        @Email
        @Size(min = 5, max = 255)
        String email
) implements Serializable {}
