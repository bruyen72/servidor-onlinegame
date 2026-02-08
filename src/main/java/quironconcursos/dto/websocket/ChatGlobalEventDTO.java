package quironconcursos.dto.websocket;

import java.io.Serializable;
import java.time.Instant;

public record ChatGlobalEventDTO(
        String username,
        String message,
        Instant timestamp
) implements Serializable {}
