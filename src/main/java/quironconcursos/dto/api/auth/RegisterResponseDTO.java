package quironconcursos.dto.api.auth;

import java.io.Serializable;

public record RegisterResponseDTO(
        String accessToken
) implements Serializable {}
