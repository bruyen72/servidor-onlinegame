package quironconcursos.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GameEntity {

    @EqualsAndHashCode.Include
    private final UUID id;

    private final AtomicInteger playersReady;
    private final AtomicInteger playersVictory;

    private final Map<String, WebSocketSession> sessions;

    public GameEntity() {
        this.id = UUID.randomUUID();

        this.playersReady = new AtomicInteger(0);
        this.playersVictory = new AtomicInteger(0);

        this.sessions = new ConcurrentHashMap<>();
    }

}
