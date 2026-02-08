package quironconcursos.services.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import quironconcursos.dto.websocket.WebSocketMessageDTO;

@Service
public class WebSocketMessageService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionService sessionService;

    public WebSocketMessageDTO parseIncomingMessage(TextMessage textMessage) throws Exception {
        return objectMapper.readValue(textMessage.getPayload(), WebSocketMessageDTO.class);
    }

    public <T> T convertMessageData(WebSocketMessageDTO message, Class<T> dataType) {
        return objectMapper.convertValue(message.data(), dataType);
    }

    public void sendToSession(WebSocketSession session, WebSocketMessageDTO message) throws Exception {
        String json = objectMapper.writeValueAsString(message);

        if (session != null && session.isOpen()) {
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        }
    }

    public void broadcastToAll(WebSocketMessageDTO message) throws Exception {
        String json = objectMapper.writeValueAsString(message);

        for (WebSocketSession session : sessionService.getSessions().values()) {
            if (session != null && session.isOpen()) {
                synchronized (session) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(json));
                    }
                }
            }
        }
    }

}
