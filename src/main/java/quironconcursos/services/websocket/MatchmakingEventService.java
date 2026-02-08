package quironconcursos.services.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import quironconcursos.dto.websocket.MatchmakingEventDTO;
import quironconcursos.dto.websocket.WebSocketMessageDTO;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MatchmakingEventService implements WebSocketEventService {

    private final Queue<String> players = new ConcurrentLinkedQueue<>();

    @Autowired
    private SessionService sessionService;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private GameEventService gameEventService;

    @Override
    public String getEvent() {
        return "MATCHMAKING_EVENT";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Code
    }

    @Override
    public void handleTextMessage(WebSocketSession session, WebSocketMessageDTO message) throws Exception {
        Map<String, Object> attributes = session.getAttributes();

        if (!attributes.containsKey("game_id")) {
            String username = (String) attributes.get("username");
            MatchmakingEventDTO matchmakingEventDTO = webSocketMessageService.convertMessageData(message, MatchmakingEventDTO.class);

            if (matchmakingEventDTO.join()) {
                if (!players.contains(username)) {
                    players.add(username);
                }
            } else {
                players.remove(username);
            }

            this.checkForMatch();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");

        players.remove(username);
    }

    private void checkForMatch() throws Exception {
        String[] matchPlayers = null;

        synchronized (players) {
            int queueSize = players.size();
            if (queueSize >= 2) {
                // Pega o máximo de jogadores disponíveis, limitado a 4
                int count = Math.min(queueSize, 4);
                matchPlayers = new String[count];

                for (int i = 0; i < count; i++) {
                    matchPlayers[i] = players.poll();
                }
            }
        }

        if (matchPlayers != null) {
            gameEventService.createGame(matchPlayers);
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void cleanupDeadPlayersFromQueue() {
        players.removeIf(username -> {
            WebSocketSession session = sessionService.getSessions().get(username);
            return session == null || !session.isOpen();
        });
    }

}
