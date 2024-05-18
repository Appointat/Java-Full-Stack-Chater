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

    private final long id;
    private final String tittle;
    private final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

    public WebSocketHandler(long id, String tittle) {
        this.id = id;
        this.tittle = tittle;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String received_message = (String) message.getPayload();
        MessageSocket message_socket = mapper.readValue(received_message, MessageSocket.class);

        session.sendMessage(new TextMessage(message_socket.getUser() + " : " + message_socket.getMessage()));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage("Welcome to " + this.tittle));
        logger.info("Connected to " + this.tittle);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("Disconnected to " + this.tittle);
    }
}
