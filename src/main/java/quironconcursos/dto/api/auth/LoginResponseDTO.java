package quironconcursos.dto.api.auth;

import java.io.Serializable;

public record LoginResponseDTO(
        String accessToken
) implements Serializable {}
