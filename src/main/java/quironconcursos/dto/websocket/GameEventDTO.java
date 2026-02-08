package quironconcursos.dto.websocket;

import java.io.Serializable;

public record GameEventDTO(
        String event,
        String playerUsername,
        String playerAction,
        int playersVictory,
        float playerPositionZ
) implements Serializable {}
