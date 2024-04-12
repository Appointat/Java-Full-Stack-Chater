package full.stack.chatter.controller;

import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.ChatRoom;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
public class ApiController {
    @Resource
    private UserAndRoomManagementRequest userAndRoomManagementRequest;

    /*
    APIs for NormalUser
     */
    @RequestMapping(value = "/create-user")
    public String createNormalUser(String first_name, String last_name, String email, String password, Boolean admin) {
        if (admin != null && admin) {
            AdminUser admin_user = new AdminUser();
            admin_user.setUser(first_name, last_name, email, password, false);
            userAndRoomManagementRequest.addAdminUser(admin_user);
        } else {
            NormalUser normal_user = new NormalUser();
            normal_user.setUser(first_name, last_name, email, password, false);
            userAndRoomManagementRequest.addNormalUser(normal_user);
        }
        return "redirect:/signin";
    }

    @PostMapping(value = "/remove-normal-user") // TODO: to be tested
    public void removeNormalUser() {
        NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(1L);

        // Remove all the created chat rooms
        for (Long chat_room_id : normal_user.getCreatedChatRooms()) {
            ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(chat_room_id);
            this.removeChatRoom(); // input: chat_room_id // it removes user from the invited users firstly
        }

        userAndRoomManagementRequest.removeOneUser(normal_user.getId());
    }

    @GetMapping(value = "/list-normal-users")
    public List<NormalUser> getNormalUsers() {
        return userAndRoomManagementRequest.getNormalUsers();
    }

    /*
    APIs for AdminUser
     */
    @PostMapping(value = "/create-admin-user")
    public void createAdminUser() {
        AdminUser admin_user = new AdminUser();
        admin_user.setUser("Cédric", "Martinet", "bcdf@pp.com", "1234", false);
        userAndRoomManagementRequest.addAdminUser(admin_user);
    }

    @PostMapping(value = "/remove-admin-user") // TODO: to be tested
    public void removeAdminUser() {
        AdminUser admin_user = userAndRoomManagementRequest.getOneAdminUser(1L);

        // Remove all the created chat rooms
        for (Long chat_room_id : admin_user.getCreatedChatRooms()) {
            ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(chat_room_id);
            this.removeChatRoom(); // input: chat_room_id // it removes user from the invited users firstly
        }

        userAndRoomManagementRequest.removeAdminUser(admin_user);
    }

    @GetMapping(value = "/list-admin-users")
    public List<AdminUser> getAdminUsers() {
        return userAndRoomManagementRequest.getAdminUsers();
    }

    /*
    APIs for NormalUser and AdminUser (User)
     */
    @PostMapping(value = "/log-in-user")
    public void logInUser() {
        NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(1L); // input of the api, suppose the login user is a normal user
//        if (normal_user instanceof NormalUser user) {
//            // TODO: if the user is normal user, activate the user in the normal user lists (postgresSQL)
//        } else {
//            // TODO: if the user is admin user, activate the user in the admin user lists (postgresSQL)
//        }
        List<NormalUser> normal_users = userAndRoomManagementRequest.getNormalUsers();
        for (NormalUser nu : normal_users) {
            if (Objects.equals(nu.getFirstName(), normal_user.getFirstName()) && Objects.equals(nu.getLastName(), normal_user.getLastName()) && Objects.equals(nu.getEmail(), normal_user.getEmail())) {
                if (!nu.getIsActive() && Objects.equals(nu.getPassword(), normal_user.getPassword())) {
                    nu.setIsActive(true);
                    userAndRoomManagementRequest.updateNormalUser(nu);
                } else {
                    System.out.println("User is already login or password is incorrect");
                }
            } else {
                System.out.println("User not found");
            }
        }
    }

    @PostMapping(value = "/log-out-user")
    public void logOutUser() {
        NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(1L); // input of the api, suppose the login user is a normal user
//        if (normal_user instanceof NormalUser user) {
        // TODO: if the user is normal user, deactivate the user in the normal user lists (postgresSQL)
//        } else {
        // TODO: if the user is admin user, deactivate the user in the admin user lists (postgresSQL)
//        }
        List<NormalUser> normal_users = userAndRoomManagementRequest.getNormalUsers();
        for (NormalUser nu : normal_users) {
            if (Objects.equals(nu.getFirstName(), normal_user.getFirstName()) && Objects.equals(nu.getLastName(), normal_user.getLastName()) && Objects.equals(nu.getEmail(), normal_user.getEmail())) {
                if (nu.getIsActive()) {
                    nu.setIsActive(false);
                    userAndRoomManagementRequest.updateNormalUser(nu);
                } else {
                    System.out.println("User is already logout");
                }
            } else {
                System.out.println("User not found");
            }
        }
    }

    /*
    APIs for ChatRoom
     */
    @PostMapping(value = "/create-chat-room")
    public void createChatRoom() {
        NormalUser normalUser = userAndRoomManagementRequest.getOneNormalUser(1L);

        ChatRoom chat_room = new ChatRoom();
        LocalDateTime create_date = LocalDateTime.now();
        LocalDateTime expire_date = create_date.plusDays(1);
        chat_room.setChatRoom("ChatRoom1", "Description1", normalUser, create_date, expire_date);

        userAndRoomManagementRequest.addChatRoom(chat_room);

        normalUser.addCreatedChatRoom(chat_room.getId());
        userAndRoomManagementRequest.updateNormalUser(normalUser);
    }

    @GetMapping(value = "/list-chat-rooms")
    public List<ChatRoom> getChatRooms() {
        return userAndRoomManagementRequest.getChatRooms();
    }

    @PostMapping(value = "/remove-chat-room") // TODO: to be tested
    public void removeChatRoom() {
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
    public void inviteUserToChatRoom() {
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
