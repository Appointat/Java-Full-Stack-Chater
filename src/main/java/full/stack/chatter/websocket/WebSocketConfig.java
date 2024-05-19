package full.stack.chatter.websocket;

import full.stack.chatter.model.ChatRoom;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private UserAndRoomManagementRequest userAndRoomManagementRequest;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Establish the websocket for each existing chat room
        List<ChatRoom> chat_rooms = userAndRoomManagementRequest.getChatRooms();
        for (ChatRoom chat_room : chat_rooms) {
            long id = chat_room.getId();
            registry.addHandler(new WebSocketHandler(chat_room), "/chatroom/" + id).setAllowedOrigins("*");
        }

        // Test the websocket and the html
        ChatRoom chat_room = new ChatRoom();
        String title_test = "api_test_chat_room";
        long id_test = 100;
        chat_room.setChatRoom(title_test, "This is a test chat room", null, null, null);
        registry.addHandler(new WebSocketHandler(chat_room), "/chatroom/" + id_test).setAllowedOrigins("*");
    }
}
