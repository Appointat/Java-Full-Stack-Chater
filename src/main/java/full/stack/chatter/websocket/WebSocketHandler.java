package full.stack.chatter.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import full.stack.chatter.model.ChatRoom;
import org.jboss.logging.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler extends TextWebSocketHandler {

    private final ChatRoom chat_room;
    private final String tittle;
    private final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());
    private final List<WebSocketSession> sessions;
    private final List<MessageSocket> message_stockets_history;

    public WebSocketHandler(ChatRoom chat_room) {
        this.chat_room = chat_room;
        this.tittle = chat_room.getTitle();
        this.message_stockets_history = new ArrayList<>();
        this.sessions = new ArrayList<>();
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String received_message = (String) message.getPayload();
        MessageSocket message_socket = mapper.readValue(received_message, MessageSocket.class);

        // Save the message in the chat room
        this.message_stockets_history.add(message_socket);

        // Send the message to all the connections/users in the chat room
        this.broadcastMessage(message_socket);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        sessions.add(session);
        logger.info(session.getId());

        for (MessageSocket message_socket : this.message_stockets_history) {
            session.sendMessage(new TextMessage(message_socket.toString()));
        }

        logger.info("Connected to " + this.tittle);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        logger.info("Disconnected to " + this.tittle);
    }

    public void broadcastMessage(MessageSocket message_socket) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(message_socket);

        for (WebSocketSession session : this.sessions) {
            session.sendMessage(new TextMessage(message));
        }
    }
}
