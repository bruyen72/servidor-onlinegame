package quironconcursos.dto.api.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record SendEmailRequestDTO(
        @NotBlank
        @Size(min = 3, max = 255)
        String title,

        @NotBlank
        @Size(max = 10000)
        String content
)
implements Serializable {}
