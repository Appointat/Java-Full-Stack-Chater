package full.stack.chatter.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import full.stack.chatter.model.ChatRoom;
import org.jboss.logging.Logger;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class WebSocketHandler extends TextWebSocketHandler {

    private final ChatRoom chat_room;
    private final String title;
    private final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());
    private final List<WebSocketSession> sessions;
    private final List<MessageSocket> message_sockets_history;

    public WebSocketHandler(ChatRoom chat_room) {
        this.chat_room = chat_room;
        this.title = chat_room.getTitle();
        this.message_sockets_history = new ArrayList<>();
        this.sessions = new ArrayList<>();
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String received_message = message.getPayload();
        MessageSocket message_socket = mapper.readValue(received_message, MessageSocket.class);
        System.out.println("Received message from user " + message_socket.getEmail() + ": " + message_socket.getMessage());

        // Save the message in the chat room
        this.message_sockets_history.add(message_socket);

        // Send the message to all the connections/users in the chat room
        this.broadcastMessage(message_socket);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws IOException {
        sessions.add(session);
        logger.info(session.getId());

        for (MessageSocket message_socket : this.message_sockets_history) {
            session.sendMessage(new TextMessage(message_socket.toString()));
        }

        logger.info("Connected to " + this.title);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session);
        logger.info("Disconnected from " + this.title);
    }

    public void broadcastMessage(MessageSocket message_socket) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(message_socket);

        for (WebSocketSession session : this.sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}
