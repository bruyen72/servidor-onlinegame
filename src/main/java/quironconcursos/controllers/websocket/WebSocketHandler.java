package quironconcursos.controllers.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import quironconcursos.dto.websocket.WebSocketMessageDTO;
import quironconcursos.services.common.JWTService;
import quironconcursos.services.websocket.*;

import java.util.Map;
import java.util.Optional;

@Service
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private PingPongEventService pingPongEventService;

    @Autowired
    private ServerInfoEventService serverInfoEventService;

    @Autowired
    private ChatGlobalEventService chatGlobalEventService;

    @Autowired
    private MatchmakingEventService matchmakingEventService;

    @Autowired
    private GameEventService gameEventService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            Optional<String> ticket = Optional
                    .ofNullable(session.getUri())
                    .map(UriComponentsBuilder::fromUri)
                    .map(UriComponentsBuilder::build)
                    .map(UriComponents::getQueryParams)
                    .map(it -> it.get("ticket"))
                    .flatMap(it -> it.stream().findFirst())
                    .map(String::trim);

            if (ticket.isPresent() && jwtService.validateTicketToken(ticket.get())) {
                String username = jwtService.getUsername(ticket.get());

                session.getAttributes().put("username", username);

                sessionService.addSession(session);
                serverInfoEventService.afterConnectionEstablished(session);
            } else {
                session.close(CloseStatus.POLICY_VIOLATION.withReason("Invalid ticket"));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            WebSocketMessageDTO webSocketMessageDTO = webSocketMessageService.parseIncomingMessage(message);

            switch (webSocketMessageDTO.event()) {
                case "PING_PONG_EVENT":
                    pingPongEventService.handleTextMessage(session, webSocketMessageDTO);
                    break;
                case "SERVER_INFO_EVENT":
                    serverInfoEventService.handleTextMessage(session, webSocketMessageDTO);
                    break;
                case "CHAT_GLOBAL_EVENT":
                    chatGlobalEventService.handleTextMessage(session, webSocketMessageDTO);
                    break;
                case "MATCHMAKING_EVENT":
                    matchmakingEventService.handleTextMessage(session, webSocketMessageDTO);
                    break;
                case "GAME_EVENT":
                    gameEventService.handleTextMessage(session, webSocketMessageDTO);
                    break;
                default:
                    webSocketMessageService.sendToSession(session, new WebSocketMessageDTO("ERROR_EVENT", "Event not found"));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            Map<String, Object> attributes = session.getAttributes();

            if (attributes.containsKey("username")) {
                sessionService.removeSession(session);

                serverInfoEventService.afterConnectionClosed(session, status);
                matchmakingEventService.afterConnectionClosed(session, status);
                gameEventService.afterConnectionClosed(session, status);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
