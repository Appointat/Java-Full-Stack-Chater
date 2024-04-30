package full.stack.chatter.controller;

import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.ChatRoom;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.model.User;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping(value = "/chat-room")
public class ChatRoomController {
    @Resource
    private UserAndRoomManagementRequest userAndRoomManagementRequest;

    @RequestMapping(value = "/create-chat-room")
    public void createChatRoom(User creator, String chat_room_name, String description, LocalDateTime create_date, LocalDateTime expire_date) {
        // Set/create a chat room
        ChatRoom chat_room = new ChatRoom();
        chat_room.setChatRoom(chat_room_name, description, creator, create_date, expire_date);

        // Update the chat room in the database
        userAndRoomManagementRequest.addChatRoom(chat_room);

        // Update the creator, whose `created_chat_rooms` field should be updated
        creator.addCreatedChatRoom(chat_room.getId());
        if (creator instanceof NormalUser normal_user) {
            userAndRoomManagementRequest.updateNormalUser(normal_user);
        } else if (creator instanceof AdminUser admin_user) {
            userAndRoomManagementRequest.updateAdminUser((AdminUser) admin_user);
        } else {
            throw new RuntimeException("Invalid user type");
        }
    }

    @GetMapping(value = "/list-chat-rooms")
    public List<ChatRoom> getChatRooms() {
        return userAndRoomManagementRequest.getChatRooms();
    }

    @PostMapping(value = "/remove-chat-room") // TODO: to be tested
    public void removeChatRoom() { // TODO: to add the input parameters to identify the chat room
        ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(1L);

        // Remove all the invited users
        for (NormalUser normal_user : chat_room.getNormalUsers()) {
            normal_user.removeInvitedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateNormalUser(normal_user);
        }
        for (AdminUser admin_user : chat_room.getAdminUsers()) {
            admin_user.removeInvitedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateAdminUser(admin_user);
        }

        // Remove the creator
        if (chat_room.getCreator() instanceof NormalUser normal_user) {
            normal_user.removeCreatedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateNormalUser(normal_user);
        } else {
            AdminUser admin_user = (AdminUser) chat_room.getCreator();
            admin_user.removeCreatedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateAdminUser(admin_user);
        }

        userAndRoomManagementRequest.removeChatRoom(chat_room);
    }

    @PostMapping(value = "/invite-user-to-chat-room")
    public void inviteUserToChatRoom() { // TODO: to add the input parameters to identify the chat room and the user
        // TODO: just used for the test of postgreSQL
        AdminUser admin_user = userAndRoomManagementRequest.getOneAdminUser(1L);
        NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(1L);
        ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(3L);

        admin_user.addUserToChatRoom(normal_user, chat_room); // Only admin can invite user
        userAndRoomManagementRequest.updateChatRoom(chat_room);

        normal_user.addInvitedChatRoom(chat_room.getId());
        userAndRoomManagementRequest.updateNormalUser(normal_user);
    }

    @PostMapping(value = "/remove-user-from-chat-room")
    public void removeUserFromChatRoom() {
        // TODO: just used for the test of postgreSQL
        AdminUser admin_user = userAndRoomManagementRequest.getOneAdminUser(1L);
        NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(1L);
        ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(3L);

        admin_user.removeUserFromChatRoom(normal_user, chat_room); // Only admin can remove user
        userAndRoomManagementRequest.updateChatRoom(chat_room);

        normal_user.removeInvitedChatRoom(chat_room.getId());
        userAndRoomManagementRequest.updateNormalUser(normal_user);
    }
}
