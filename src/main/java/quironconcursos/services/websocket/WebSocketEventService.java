package quironconcursos.services.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import quironconcursos.dto.websocket.WebSocketMessageDTO;

public interface WebSocketEventService {

    String getEvent();

    void afterConnectionEstablished(WebSocketSession session) throws Exception;

    void handleTextMessage(WebSocketSession session, WebSocketMessageDTO message) throws Exception;

    void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception;

}
