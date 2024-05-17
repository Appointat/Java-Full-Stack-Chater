package full.stack.chatter.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class WebSocketHandler extends TextWebSocketHandler {

    private final String nameChat;
    private final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

    public WebSocketHandler(String nameChat) {
        this.nameChat = nameChat;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        String receivedMessage = (String) message.getPayload();
        MessageSocket messageSocket = mapper.readValue(receivedMessage, MessageSocket.class);

        session.sendMessage(new TextMessage(messageSocket.getUser() + " : " + messageSocket.getMessage()));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage("Bienvenue sur " + this.nameChat));
        logger.info("Connecté sur le " + this.nameChat);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("Déconnecté du " + this.nameChat);
    }
}
