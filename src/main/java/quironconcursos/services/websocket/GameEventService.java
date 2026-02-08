package quironconcursos.services.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import quironconcursos.dto.websocket.GameEventDTO;
import quironconcursos.dto.websocket.WebSocketMessageDTO;
import quironconcursos.entities.GameEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class GameEventService implements WebSocketEventService {

    private final Map<UUID, GameEntity> games = new ConcurrentHashMap<>();

    @Autowired
    private SessionService sessionService;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Override
    public String getEvent() {
        return "GAME_EVENT";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Code
    }

    @Override
    public void handleTextMessage(WebSocketSession session, WebSocketMessageDTO message) throws Exception {
        Map<String, Object> attributes = session.getAttributes();

        if (attributes.containsKey("game_id")) {
            String username = (String) attributes.get("username");
            String gameId = (String) attributes.get("game_id");

            GameEntity gameEntity = games.get(UUID.fromString(gameId));
            GameEventDTO gameEventDTO = webSocketMessageService.convertMessageData(message, GameEventDTO.class);

            if (gameEventDTO.event().equals("PLAYER_ACTION")) {
                switch (gameEventDTO.playerAction()) {
                    case "IDLE", "RUN", "SPRINT", "JUMP", "ROLL" -> {
                        GameEventDTO responsePlayerAction = new GameEventDTO("PLAYER_ACTION", username, gameEventDTO.playerAction(), gameEntity.getPlayersVictory().get(), gameEventDTO.playerPositionZ());

                        for (WebSocketSession s : gameEntity.getSessions().values()) {
                            webSocketMessageService.sendToSession(s, new WebSocketMessageDTO(this.getEvent(), responsePlayerAction));
                        }
                    }
                    case "VICTORY" -> {
                        int victoryCount = gameEntity.getPlayersVictory().incrementAndGet();
                        GameEventDTO responsePlayerAction = new GameEventDTO("PLAYER_ACTION", username, "VICTORY", victoryCount, gameEventDTO.playerPositionZ());

                        for (WebSocketSession s : gameEntity.getSessions().values()) {
                            webSocketMessageService.sendToSession(s, new WebSocketMessageDTO(this.getEvent(), responsePlayerAction));
                        }

                        gameEntity.getSessions().remove(username);
                        attributes.remove("game_id");

                        if (gameEntity.getSessions().isEmpty()) {
                            games.remove(gameEntity.getId());
                        }
                    }
                    case "READY" -> {
                        gameEntity.getPlayersReady().incrementAndGet();

                        if (gameEntity.getPlayersReady().get() == gameEntity.getSessions().size()) {
                            String usernames = gameEntity.getSessions().values().stream()
                                    .map(s -> (String) s.getAttributes().get("username"))
                                    .collect(Collectors.joining(";"));

                            GameEventDTO responseGameStart = new GameEventDTO("GAME_START", usernames, "", gameEntity.getPlayersVictory().get(), 0.0f);

                            for (WebSocketSession s : gameEntity.getSessions().values()) {
                                webSocketMessageService.sendToSession(s, new WebSocketMessageDTO(this.getEvent(), responseGameStart));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attributes = session.getAttributes();

        if (attributes.containsKey("game_id")) {
            String username = (String) attributes.get("username");
            String gameId = (String) attributes.get("game_id");
            GameEntity gameEntity = games.get(UUID.fromString(gameId));

            gameEntity.getSessions().remove(username);

            if (gameEntity.getSessions().isEmpty()) {
                games.remove(gameEntity.getId());
            } else {
                GameEventDTO gameEventDTO = new GameEventDTO("PLAYER_ACTION", username, "QUIT", gameEntity.getPlayersVictory().get(), 0.0f);

                for (WebSocketSession s : gameEntity.getSessions().values()) {
                    webSocketMessageService.sendToSession(s, new WebSocketMessageDTO(this.getEvent(), gameEventDTO));
                }
            }
        }
    }

    public void createGame(String[] players) throws Exception {
        GameEntity gameEntity = new GameEntity();

        for (String username : players) {
            WebSocketSession playerSession = sessionService.getSessions().get(username);

            if (playerSession != null) {
                playerSession.getAttributes().put("game_id", gameEntity.getId().toString());

                gameEntity.getSessions().put(username, playerSession);
            }
        }

        if (!gameEntity.getSessions().isEmpty()) {
            games.put(gameEntity.getId(), gameEntity);

            GameEventDTO gameEventDTO = new GameEventDTO("GAME_CREATED", "", "", gameEntity.getPlayersVictory().get(), 0.0f);

            for (WebSocketSession session : gameEntity.getSessions().values()) {
                webSocketMessageService.sendToSession(session, new WebSocketMessageDTO(this.getEvent(), gameEventDTO));
            }
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void cleanupOrphanGames() {
        games.entrySet().removeIf(entry -> {
            GameEntity game = entry.getValue();

            game.getSessions().entrySet().removeIf(sessionEntry -> {
                WebSocketSession session = sessionEntry.getValue();
                boolean isDead = session == null || !session.isOpen();

                if (isDead) {
                    try {
                        if (session != null) {
                            session.getAttributes().remove("game_id");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }

                return isDead;
            });

            return game.getSessions().isEmpty();
        });
    }

}
