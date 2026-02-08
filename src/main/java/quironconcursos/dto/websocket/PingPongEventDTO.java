package quironconcursos.dto.websocket;

import java.io.Serializable;

public record PingPongEventDTO(
        String message
) implements Serializable {}
