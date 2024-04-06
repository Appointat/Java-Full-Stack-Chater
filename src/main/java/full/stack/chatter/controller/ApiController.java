package full.stack.chatter.controller;

import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.ChatRoom;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.services.ServicesRequest;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ApiController {
    @Resource
    private ServicesRequest servicesRequest;

    /*
    APIs for NormalUser
     */
    @PostMapping(value = "/create-normal-user")
    public void createNormalUser() {
        NormalUser normal_user = new NormalUser();
        normal_user.setUser("Cédric", "Martinet", "aa@pp.com", "1234", false);
        servicesRequest.addNormalUser(normal_user);
    }

    @GetMapping(value = "/list-normal-users")
    public List<NormalUser> getNormalUsers() {
        return servicesRequest.getNormalUsers();
    }

    /*
    APIs for AdminUser
     */
    @PostMapping(value = "/create-admin-user")
    public void createAdminUser() {
        AdminUser admin_user = new AdminUser();
        admin_user.setUser("Cédric", "Martinet", "bcdf@pp.com", "1234", false);
        servicesRequest.addAdminUser(admin_user);
    }

    @GetMapping(value = "/list-admin-users")
    public List<AdminUser> getAdminUsers() {
        return servicesRequest.getAdminUsers();
    }

    /*
    APIs for ChatRoom
     */
    @PostMapping(value = "/create-chat-room")
    public void createChatRoom() {
        NormalUser normalUser = servicesRequest.getOneUser(1L);

        ChatRoom chat_room = new ChatRoom();
        LocalDateTime create_date = LocalDateTime.now();
        LocalDateTime expire_date = create_date.plusDays(1);
        chat_room.setChatRoom("ChatRoom1", "Description1", normalUser, create_date, expire_date);

        servicesRequest.addChatRoom(chat_room);
    }

    @GetMapping(value = "/list-chat-rooms")
    public List<ChatRoom> getChatRooms() {
        return servicesRequest.getChatRooms();
    }

    @PostMapping(value = "/invite-user-to-chat-room")
    public void inviteUserToChatRoom() {
        // TODO: just used for the test of postgreSQL
        NormalUser normal_user = servicesRequest.getOneUser(1L);
        ChatRoom chat_room = servicesRequest.getOneChatRoom(3L);

        chat_room.addUser(normal_user);
        servicesRequest.updateChatRoom(chat_room);
    }

    @PostMapping(value = "/remove-user-from-chat-room")
    public void removeUserFromChatRoom() {
        // TODO: just used for the test of postgreSQL
        NormalUser normal_user = servicesRequest.getOneUser(1L);
        ChatRoom chat_room = servicesRequest.getOneChatRoom(3L);

        chat_room.removeUser(normal_user);
        servicesRequest.updateChatRoom(chat_room);
    }
}
