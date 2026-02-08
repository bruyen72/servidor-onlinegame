package quironconcursos.dto.websocket;

import java.io.Serializable;

public record WebSocketMessageDTO(
        String event,
        Object data
) implements Serializable {}
