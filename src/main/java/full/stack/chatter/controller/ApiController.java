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

        normalUser.addCreatedChatRoom(chat_room.getId());
        servicesRequest.updateNormalUser(normalUser);
    }

    @GetMapping(value = "/list-chat-rooms")
    public List<ChatRoom> getChatRooms() {
        return servicesRequest.getChatRooms();
    }

    @PostMapping(value = "/remove-chat-room") // TODO: to be tested
    public void removeChatRoom() {
        ChatRoom chat_room = servicesRequest.getOneChatRoom(1L);

        // Remove all the invited users
        for (NormalUser normal_user : chat_room.getNormalUsers()) {
            normal_user.removeInvitedChatRoom(chat_room.getId());
            servicesRequest.updateNormalUser(normal_user);
        }
        for (AdminUser admin_user : chat_room.getAdminUsers()) {
            admin_user.removeInvitedChatRoom(chat_room.getId());
            servicesRequest.updateAdminUser(admin_user);
        }

        // Remove the creator
        if (chat_room.getCreator() instanceof NormalUser normal_user) {
            normal_user.removeCreatedChatRoom(chat_room.getId());
            servicesRequest.updateNormalUser(normal_user);
        } else {
            AdminUser admin_user = (AdminUser) chat_room.getCreator();
            admin_user.removeCreatedChatRoom(chat_room.getId());
            servicesRequest.updateAdminUser(admin_user);
        }

        servicesRequest.removeChatRoom(chat_room);
    }

    @PostMapping(value = "/invite-user-to-chat-room")
    public void inviteUserToChatRoom() {
        // TODO: just used for the test of postgreSQL
        NormalUser normal_user = servicesRequest.getOneUser(1L);
        ChatRoom chat_room = servicesRequest.getOneChatRoom(3L);

        chat_room.addUser(normal_user);
        servicesRequest.updateChatRoom(chat_room);

        normal_user.addInvitedChatRoom(chat_room.getId());
        servicesRequest.updateNormalUser(normal_user);
    }

    @PostMapping(value = "/remove-user-from-chat-room")
    public void removeUserFromChatRoom() {
        // TODO: just used for the test of postgreSQL
        NormalUser normal_user = servicesRequest.getOneUser(1L);
        ChatRoom chat_room = servicesRequest.getOneChatRoom(3L);

        chat_room.removeUser(normal_user);
        servicesRequest.updateChatRoom(chat_room);

        normal_user.removeInvitedChatRoom(chat_room.getId());
        servicesRequest.updateNormalUser(normal_user);
    }
}
