package quironconcursos.dto.websocket;

import java.io.Serializable;

public record MatchmakingEventDTO(
        boolean join
) implements Serializable {}
