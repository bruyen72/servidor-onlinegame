package quironconcursos.dto.websocket;

import java.io.Serializable;

public record ServerInfoEventDTO(
        int playersOnline
) implements Serializable {}
