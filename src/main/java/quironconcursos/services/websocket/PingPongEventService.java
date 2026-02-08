package quironconcursos.services.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import quironconcursos.dto.websocket.WebSocketMessageDTO;
import quironconcursos.dto.websocket.PingPongEventDTO;

@Service
public class PingPongEventService implements WebSocketEventService {

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Override
    public String getEvent() {
        return "PING_PONG_EVENT";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Code
    }

    @Override
    public void handleTextMessage(WebSocketSession session, WebSocketMessageDTO message) throws Exception {
        PingPongEventDTO pingPongEventDTO = new PingPongEventDTO("Pong");

        webSocketMessageService.sendToSession(session, new WebSocketMessageDTO(this.getEvent(), pingPongEventDTO));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Code
    }

}
