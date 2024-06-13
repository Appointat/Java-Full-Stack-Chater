package full.stack.chatter.controller;

import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.ChatRoom;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.utils.AuthUtils;
import full.stack.chatter.services.EmailService;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("user")
public class UserController {

    private final EmailService emailService;

    @Resource
    private UserAndRoomManagementRequest userAndRoomManagementRequest;


    public UserController(UserAndRoomManagementRequest userAndRoomManagementRequest, EmailService emailService) {
        this.userAndRoomManagementRequest = userAndRoomManagementRequest;
        this.emailService = emailService;
    }

    //for springboot signup
    @RequestMapping("signup")
    public String signup(String first_name, String last_name, String email, String password, String code, Boolean is_admin, HttpSession session) {
        String sessionCode = (String) session.getAttribute("code");
        if(!sessionCode.equals(code)) {
            return "redirect:/signup";
        }
        password= AuthUtils.encryptPassword(password);
        try {
            if (is_admin != null && is_admin) {
                AdminUser user = new AdminUser(first_name, last_name, email, password);
                userAndRoomManagementRequest.addAdminUser(user);
            } else {
                NormalUser user = new NormalUser(first_name, last_name, email, password);
                userAndRoomManagementRequest.addNormalUser(user);
            }
        } catch (Exception e) {     //user already exist
            e.printStackTrace();
            return "redirect:/signup";
        }                       //success
        return "redirect:/signin";
    }

    //for springboot create an account by an admin user with an email
    @RequestMapping("admin_create")
    public String admin_create(String email, Boolean is_admin, String password, HttpSession session) {
        session.removeAttribute("notice");
        session.setAttribute("notice", "success");
        String cpassword= AuthUtils.encryptPassword(password);
        try {
            if (is_admin != null && is_admin) {
                AdminUser user = new AdminUser("temp", "temp", email, cpassword);
                user.setIs_new(true);
                userAndRoomManagementRequest.addAdminUser(user);
                emailService.sendConfirmationEmail(email, password);
            } else {
                NormalUser user = new NormalUser("temp", "temp", email, cpassword);
                user.setIs_new(true);
                userAndRoomManagementRequest.addNormalUser(user);
                emailService.sendConfirmationEmail(email, password);
            }
        } catch (Exception e) {     //user with same email and same type already exist
            e.printStackTrace();
            session.setAttribute("notice", "failed");
        }
        return "redirect:/page_admin_create";
    }

    //for springboot signin
    @RequestMapping("signin")
    public String signin(String email, String password, Boolean is_admin, HttpSession session) {
        session.removeAttribute("email");
        session.removeAttribute("error");
        session.removeAttribute("created_rooms");
        if (is_admin != null && is_admin) {
            try {   //search admin user by email
                Long admin_user_id = userAndRoomManagementRequest.findAdminUserIdByEmail(email);
                AdminUser admin_user = userAndRoomManagementRequest.getOneAdminUser(admin_user_id);
                if (admin_user != null && admin_user.getIsActive()) {
                    if (AuthUtils.matchesPassword(password,admin_user.getPassword())) {    //password correct
                        admin_user.setFailed_attempt(0);                //reset attempt
                        userAndRoomManagementRequest.updateAdminUser(admin_user);   //update attempt to database
                        session.setAttribute("user", admin_user);
                        session.setAttribute("is_admin", true);
                        if (admin_user.getIs_new()) {    //if user is created by an admin and first signin
                            return "redirect:/page_first_login";
                        }
                        //get created and invited rooms
                        List<ChatRoom> created_rooms = userAndRoomManagementRequest.getCreatedChatRoomsByAdminID(admin_user_id);
                        session.setAttribute("created_rooms", created_rooms);
                        List<ChatRoom> invited_rooms = userAndRoomManagementRequest.getInvitedChatRoomsByAdminID(admin_user_id);
                        session.setAttribute("invited_rooms", invited_rooms);
                        return "redirect:/page_admin";
                    } else {    //password wrong
                        int attempts = admin_user.getFailed_attempt() + 1;
                        admin_user.setFailed_attempt(attempts);
                        if (attempts >= 3) {    //dis-activate the account
                            admin_user.setIsActive(false);
                        }
                        userAndRoomManagementRequest.updateAdminUser(admin_user);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/signin";
            }
        } else {    //same logic as admin user
            try {
                Long normal_user_id = userAndRoomManagementRequest.findNormalUserIdByEmail(email);
                NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(normal_user_id);
                if (normal_user != null && normal_user.getIsActive()) {
                    if (AuthUtils.matchesPassword(password,normal_user.getPassword())) {
                        normal_user.setFailed_attempt(0);
                        userAndRoomManagementRequest.updateNormalUser(normal_user);
                        session.setAttribute("user", normal_user);
                        session.setAttribute("is_admin", false);
                        if (normal_user.getIs_new()) {
                            return "redirect:/page_first_login";
                        }
                        List<ChatRoom> created_rooms = userAndRoomManagementRequest.getCreatedChatRoomsByNormalID(normal_user_id);
                        session.setAttribute("created_rooms", created_rooms);
                        List<ChatRoom> invited_rooms = userAndRoomManagementRequest.getInvitedChatRoomsByNormalID(normal_user_id);
                        session.setAttribute("invited_rooms", invited_rooms);
                        return "redirect:/page_normaluser";
                    } else {
                        int attempts = normal_user.getFailed_attempt() + 1;
                        normal_user.setFailed_attempt(attempts);
                        if (attempts >= 3) {
                            normal_user.setIsActive(false);
                        }
                        userAndRoomManagementRequest.updateNormalUser(normal_user);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/signin";
            }
        }
        session.setAttribute("error", "wrong or abnormal account");
        return "redirect:/signin";
    }

    //for springboot update information after first login of a user created by an admin with email
    @RequestMapping("first_login")
    public String first_login(String first_name, String last_name, String email, String password, Boolean is_admin, HttpSession session) {
        password= AuthUtils.encryptPassword(password);
        if (is_admin != null && is_admin) {
            AdminUser user = userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
            user.setIs_new(false);
            user.setFirst_name(first_name);
            user.setLast_name(last_name);
            user.setPassword(password);
            userAndRoomManagementRequest.updateAdminUser(user);
            session.setAttribute("user", user);
            return "redirect:/page_admin";
        } else {
            NormalUser user = userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
            user.setIs_new(false);
            user.setFirst_name(first_name);
            user.setLast_name(last_name);
            user.setPassword(password);
            userAndRoomManagementRequest.updateNormalUser(user);
            session.setAttribute("user", user);
            return "redirect:/page_normaluser";
        }
    }

    //for springboot edit an account and update to database
    @RequestMapping("edit")
    public String edit(String first_name, String last_name, String email, String password, Boolean is_admin) {
        password= AuthUtils.encryptPassword(password);
        if (is_admin != null && is_admin) {
            AdminUser user = userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
            user.setFirst_name(first_name);
            user.setLast_name(last_name);
            user.setPassword(password);
            userAndRoomManagementRequest.updateAdminUser(user);
            return "/page_signin";
        } else {
            NormalUser user = userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
            user.setFirst_name(first_name);
            user.setLast_name(last_name);
            user.setPassword(password);
            userAndRoomManagementRequest.updateNormalUser(user);
            return "/page_signin";
        }
    }

    //for springboot admin user get the user list
    @RequestMapping("userlist")
    public String userlist(Model model) {
        List<NormalUser> normalUserList = userAndRoomManagementRequest.getNormalUsers();
        List<AdminUser> adminUserList = userAndRoomManagementRequest.getAdminUsers();

        model.addAttribute("normalUserList", normalUserList);
        model.addAttribute("adminUserList", adminUserList);
        return "page_userlist";
    }

    //for springboot admin user delete a normal user account
    @RequestMapping("normaldelete")
    public String normaldelete(String email) {
        userAndRoomManagementRequest.removeOneUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
        return "redirect:/user/userlist";
    }

    //for springboot admin user delete an admin user account
    @RequestMapping("admindelete")
    public String admindelete(String email) {
        userAndRoomManagementRequest.removeAdminUser(userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email)));
        return "redirect:/user/userlist";
    }

    //for springboot admin user activate or dis-activate a normal user account
    @RequestMapping("normalchangestatus")
    public String normalchangestatus(String email) {
        NormalUser user = userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
        user.setIsActive(!user.getIsActive());
        userAndRoomManagementRequest.updateNormalUser(user);
        return "redirect:/user/userlist";
    }

    //for springboot admin user activate or dis-activate a admin user account
    @RequestMapping("adminchangestatus")
    public String adminchangestatus(String email) {
        AdminUser user = userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
        user.setIsActive(!user.getIsActive());
        userAndRoomManagementRequest.updateAdminUser(user);
        return "redirect:/user/userlist";
    }

    //for springboot send email with password
    @RequestMapping("forget")
    public String forget(String email, Boolean is_admin, HttpSession session,String code) {
        session.removeAttribute("email");
        String password=AuthUtils.generateRandomPassword(9);
        String cpassword= AuthUtils.encryptPassword(password);
        String sessionCode = (String) session.getAttribute("code");

        if(!sessionCode.equals(code)) {
            session.setAttribute("email", "code wrong");
            return "redirect:/forget";
        }
        if (is_admin != null && is_admin) {
            try {
                AdminUser user = userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
                user.setPassword(cpassword);
                userAndRoomManagementRequest.updateAdminUser(user);
                emailService.sendConfirmationEmail(email, password);
                return "redirect:/signin";
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("email", "user not exist or sending failed");
                return "redirect:/forget";
            }
        } else {
            try {
                NormalUser user = userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
                user.setPassword(cpassword);
                userAndRoomManagementRequest.updateNormalUser(user);
                emailService.sendConfirmationEmail(email, password);
                return "redirect:/signin";
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("email", "user not exist or sending failed");
                return "redirect:/forget";
            }
        }
    }

    //not used
    @RestController
    public static class ApiController {
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
}
