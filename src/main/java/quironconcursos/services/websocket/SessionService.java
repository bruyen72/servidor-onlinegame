package quironconcursos.services.websocket;

import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class SessionService {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");

        sessions.put(username, session);
    }

    public void removeSession(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");

        sessions.remove(username);
    }

    @Scheduled(fixedDelay = 300000)
    public void cleanupDeadSessions() {
        sessions.entrySet().removeIf(entry -> entry.getValue() == null || !entry.getValue().isOpen());
    }

}
