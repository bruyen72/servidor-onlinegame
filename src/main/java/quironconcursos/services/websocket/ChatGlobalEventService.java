package quironconcursos.services.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import quironconcursos.dto.websocket.WebSocketMessageDTO;
import quironconcursos.dto.websocket.ChatGlobalEventDTO;

import java.time.Instant;

@Service
public class ChatGlobalEventService implements WebSocketEventService {

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Override
    public String getEvent() {
        return "CHAT_GLOBAL_EVENT";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Code
    }

    @Override
    public void handleTextMessage(WebSocketSession session, WebSocketMessageDTO message) throws Exception {
        String username = (String) session.getAttributes().get("username");
        ChatGlobalEventDTO chatGlobalEventDTO = webSocketMessageService.convertMessageData(message, ChatGlobalEventDTO.class);

        if (chatGlobalEventDTO.message().length() < 3) {
            webSocketMessageService.sendToSession(session, new WebSocketMessageDTO("ERROR_CHAT_EVENT", "The message must be at least 3 characters long"));
            return;
        }

        if (chatGlobalEventDTO.message().length() > 400) {
            webSocketMessageService.sendToSession(session, new WebSocketMessageDTO("ERROR_CHAT_EVENT", "A message cannot exceed 400 characters"));
            return;
        }

        ChatGlobalEventDTO responseChatGlobalEventDTO = new ChatGlobalEventDTO(username, chatGlobalEventDTO.message(), Instant.now());
        webSocketMessageService.broadcastToAll(new WebSocketMessageDTO(this.getEvent(), responseChatGlobalEventDTO));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Code
    }

}
