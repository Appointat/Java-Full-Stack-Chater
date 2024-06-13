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

    private static WebSocketHandlerRegistry sharedRegistry;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        sharedRegistry = registry;
        initializeWebSockets();
    }

    private void initializeWebSockets() {
        List<ChatRoom> chat_rooms = userAndRoomManagementRequest.getChatRooms();
        for (ChatRoom chat_room : chat_rooms) {
            registerWebSocketForRoom(chat_room);
        }
        for (Long i = 1L; i <= 1000; i++) {
            registerWebSocketForAll(i);
        }
    }

    public static void registerWebSocketForRoom(ChatRoom chatRoom) {
        if (sharedRegistry != null) {
            try {
                sharedRegistry.addHandler(new WebSocketHandler(chatRoom), "/ws/chatroom/" + chatRoom.getId())
                        .setAllowedOrigins("*");
            } catch (Exception e) {
                System.out.println("Error: Could not establish websocket for chat room with id: " + chatRoom.getId());
            }
        }
    }

    public static void registerWebSocketForAll(Long id) {
        if (sharedRegistry != null) {
            try {
                sharedRegistry.addHandler(new WebSocketHandler(new ChatRoom()), "/ws/chatroom/" + id)
                        .setAllowedOrigins("*");
            } catch (Exception e) {
                System.out.println("Error: Could not establish websocket for chat room with id: " + id);
            }
        }
    }
}




//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//    @Autowired
//    private UserAndRoomManagementRequest userAndRoomManagementRequest;
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        // Establish the websocket for each existing chat room
//        List<ChatRoom> chat_rooms = userAndRoomManagementRequest.getChatRooms();
//
//        for (ChatRoom chat_room : chat_rooms) {
//            long chat_room_id = chat_room.getId();
//            try {
//                registry.addHandler(new WebSocketHandler(chat_room), "/ws/chatroom/" + chat_room_id)
//                        .setAllowedOrigins("*");
//            } catch (Exception e) {
//                System.out.println("Error: Could not establish websocket for chat room with id: " + chat_room_id);
//            }
//        }
//    }
//}