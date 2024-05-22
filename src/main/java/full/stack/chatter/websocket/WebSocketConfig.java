package full.stack.chatter.websocket;

import full.stack.chatter.model.ChatRoom;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.time.LocalDateTime;
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
        NormalUser normal_user = new NormalUser();
        normal_user.setUser("test_normal_user", "Adam", "Smith", "123456", true);
        LocalDateTime create_date = LocalDateTime.now();
        LocalDateTime expire_date = create_date.plusDays(1);
        chat_room.setChatRoom(title_test, "This is a test chat room", normal_user, create_date, expire_date);
        registry.addHandler(new WebSocketHandler(chat_room), "/chatroom/" + id_test).setAllowedOrigins("*");
    }
}
