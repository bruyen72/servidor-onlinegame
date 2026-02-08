package quironconcursos.services.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import quironconcursos.dto.websocket.WebSocketMessageDTO;
import quironconcursos.dto.websocket.ServerInfoEventDTO;

@Service
public class ServerInfoEventService implements WebSocketEventService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Override
    public String getEvent() {
        return "SERVER_INFO_EVENT";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ServerInfoEventDTO serverInfoEventDTO = new ServerInfoEventDTO(sessionService.getSessions().size());

        webSocketMessageService.broadcastToAll(new WebSocketMessageDTO(this.getEvent(), serverInfoEventDTO));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, WebSocketMessageDTO message) throws Exception {
        ServerInfoEventDTO serverInfoEventDTO = new ServerInfoEventDTO(sessionService.getSessions().size());

        webSocketMessageService.sendToSession(session, new WebSocketMessageDTO(this.getEvent(), serverInfoEventDTO));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ServerInfoEventDTO serverInfoEventDTO = new ServerInfoEventDTO(sessionService.getSessions().size());

        webSocketMessageService.broadcastToAll(new WebSocketMessageDTO(this.getEvent(), serverInfoEventDTO));
    }

}
