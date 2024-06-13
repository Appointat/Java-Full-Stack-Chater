package full.stack.chatter.controller;

import full.stack.chatter.dto.CreateroomRequest;
import full.stack.chatter.dto.QuitRoomRequest;
import full.stack.chatter.dto.SignupRequest;
import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.ChatRoom;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.model.User;
import full.stack.chatter.utils.AuthUtils;
import full.stack.chatter.services.EmailService;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import full.stack.chatter.utils.VerifyCodeUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("app")
public class AppController {

    @Resource
    private UserAndRoomManagementRequest userAndRoomManagementRequest;

    @Resource
    private final EmailService emailService;

    public AppController(UserAndRoomManagementRequest userAndRoomManagementRequest, EmailService emailService) {
        this.userAndRoomManagementRequest = userAndRoomManagementRequest;
        this.emailService = emailService;
    }

    @GetMapping("signin")
    @ResponseBody
    public ResponseEntity<?> signin(@RequestParam String email, @RequestParam String password, @RequestParam Boolean is_admin) {
        try {
            if (is_admin != null && is_admin) {
                //search for admin user by email
                Long admin_user_id = userAndRoomManagementRequest.findAdminUserIdByEmail(email);
                AdminUser admin_user = userAndRoomManagementRequest.getOneAdminUser(admin_user_id);
                if (admin_user != null && admin_user.getIsActive()) {       //user exist and active
                    if (AuthUtils.matchesPassword(password,admin_user.getPassword())) {        //password correct
                        admin_user.setFailed_attempt(0);                    //reset failed attempt
                        userAndRoomManagementRequest.updateAdminUser(admin_user);   //update to database
                        return ResponseEntity.ok(admin_user);               //return admin user
                    } else {                                                //password wrong
                        handlePasswordWrong(admin_user);                    //add one attempt or dis-activate
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("password incorrect");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("account inactive");
                }
            } else {
                //search for normal user by email
                Long normal_user_id = userAndRoomManagementRequest.findNormalUserIdByEmail(email);
                NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(normal_user_id);
                if (normal_user != null && normal_user.getIsActive()) {     //user exist and active
                    if (AuthUtils.matchesPassword(password,normal_user.getPassword())) {       //password correct
                        normal_user.setFailed_attempt(0);                   //reset the failed attempt
                        userAndRoomManagementRequest.updateNormalUser(normal_user); //update database
                        return ResponseEntity.ok(normal_user);              //return normal user
                    } else {                                                //password wrong
                        handlePasswordWrong(normal_user);                   //add one attempt or dis-activate
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("password incorrect");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("account inactive");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not found");   //cannot find the user
        }
    }

    private void handlePasswordWrong(User user) {
        int attempts = user.getFailed_attempt() + 1;    //add one attempt and update to database
        user.setFailed_attempt(attempts);
        if (attempts >= 3) {                            //dis-activate the user if 3 times wrong
            user.setIsActive(false);
        }
        if (user instanceof AdminUser) {
            userAndRoomManagementRequest.updateAdminUser((AdminUser) user);
        } else if (user instanceof NormalUser) {
            userAndRoomManagementRequest.updateNormalUser((NormalUser) user);
        }
    }

    @PostMapping("signup")
    @ResponseBody
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest, HttpSession session) {
        String sessionCode = (String) session.getAttribute("code");
        if(!sessionCode.equals(signupRequest.getCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("code wrong");
        }
        String cpassword=AuthUtils.encryptPassword(signupRequest.getPassword());
        try {
            if (signupRequest.getIs_admin() != null && signupRequest.getIs_admin()) {   //add admin user to database
                AdminUser user = new AdminUser(signupRequest.getFirstname(), signupRequest.getLastname(), signupRequest.getEmail(), cpassword);
                userAndRoomManagementRequest.addAdminUser(user);
            } else {                                                                    //add normal user to database
                NormalUser user = new NormalUser(signupRequest.getFirstname(), signupRequest.getLastname(), signupRequest.getEmail(), cpassword);
                userAndRoomManagementRequest.addNormalUser(user);
            }
        } catch (Exception e) {          //exception when email already existed in database
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("account already existed");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @PostMapping("createroom")
    @ResponseBody
    public ResponseEntity<?> createroom(@RequestBody CreateroomRequest createroomRequest) {
        ChatRoom chat_room = new ChatRoom();
        if (createroomRequest.getIs_admin()) {    //room created by admin user
            AdminUser creator = userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(createroomRequest.getEmail()));
            chat_room.setChatRoom(createroomRequest.getTitle(), createroomRequest.getDescription(), creator, createroomRequest.getCreatedate(), createroomRequest.getExpiredate());
            userAndRoomManagementRequest.addChatRoom(chat_room);    //add new room
            creator.addCreatedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateAdminUser(creator);  //upload admin user
        } else {                                  //room created by normal user
            NormalUser creator = userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(createroomRequest.getEmail()));
            chat_room.setChatRoom(createroomRequest.getTitle(), createroomRequest.getDescription(), creator, createroomRequest.getCreatedate(), createroomRequest.getExpiredate());
            userAndRoomManagementRequest.addChatRoom(chat_room);    //add new room
            creator.addCreatedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateNormalUser(creator); //upload normal user
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @GetMapping("createdrooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoom>> createdrooms(@RequestParam String email, @RequestParam Boolean is_admin) {
        //get rooms created by admin or normal user
        List<ChatRoom> created_rooms;
        if (is_admin) {
            created_rooms = userAndRoomManagementRequest.getCreatedChatRoomsByAdminID(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
        } else {
            created_rooms = userAndRoomManagementRequest.getCreatedChatRoomsByNormalID(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
        }
        return ResponseEntity.ok(created_rooms);
    }

    @GetMapping("invitedrooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoom>> invitedrooms(@RequestParam String email, @RequestParam Boolean is_admin) {
        //get rooms in which user is invited
        List<ChatRoom> invited_rooms;
        if (is_admin) {
            invited_rooms = userAndRoomManagementRequest.getInvitedChatRoomsByAdminID(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
        } else {
            invited_rooms = userAndRoomManagementRequest.getInvitedChatRoomsByNormalID(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
        }
        return ResponseEntity.ok(invited_rooms);
    }

    @DeleteMapping("/deleteroom/{roomId}")
    @ResponseBody
    public ResponseEntity<?> deleteroom(@PathVariable Long roomId) {
        //remove a room from database
        userAndRoomManagementRequest.removeChatRoom(userAndRoomManagementRequest.getOneChatRoom(roomId));
        return ResponseEntity.ok("Room deleted");
    }

    @PutMapping("/invite/{roomId}/{email}/{is_admin}/{invitor_email}/{invitor_admin}")
    @ResponseBody
    public ResponseEntity<?> invite(@PathVariable Long roomId, @PathVariable String email, @PathVariable Boolean is_admin, @PathVariable String invitor_email, @PathVariable Boolean invitor_admin) {
        if (is_admin) {      //invite an admin
            if (email.equals(invitor_email) && invitor_admin) {     //same email and same type = invite the user himself
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot invite yourself");
            } else {
                try {
                    AdminUser admin_user = userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email)); //find the admin user
                    ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(roomId);   //find the chatroom
                    String notice = chat_room.addUser(admin_user);
                    userAndRoomManagementRequest.updateChatRoom(chat_room);
                    admin_user.addInvitedChatRoom(roomId);
                    userAndRoomManagementRequest.updateAdminUser(admin_user);
                    if (notice.equals("success")) {
                        return ResponseEntity.ok().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(notice);
                    }
                } catch (Exception e) {      //cannot find the user
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
                }
            }
        } else {      //invite a normal user
            if (email.equals(invitor_email) && !invitor_admin) {  //same email and same type = invite the user himself
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot invite yourself");
            } else {
                try {
                    NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email)); //find the normal user
                    ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(roomId);   //find the chatroom
                    String notice = chat_room.addUser(normal_user);
                    userAndRoomManagementRequest.updateChatRoom(chat_room);
                    normal_user.addInvitedChatRoom(roomId);
                    userAndRoomManagementRequest.updateNormalUser(normal_user);
                    if (notice.equals("success")) {
                        return ResponseEntity.ok().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(notice);
                    }
                } catch (Exception e) {  //cannot find the user
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
                }
            }
        }
    }

    @GetMapping("/forgot")
    @ResponseBody
    public ResponseEntity<?> forgot(@RequestParam String email, @RequestParam Boolean is_admin,@RequestParam String code, HttpSession session) {
        String sessionCode = (String) session.getAttribute("code");
        if(!sessionCode.equals(code)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("code wrong");
        }

        String password=AuthUtils.generateRandomPassword(9);
        String cpassword= AuthUtils.encryptPassword(password);
        if (is_admin != null && is_admin) {
            try {   //find an admin by email
                AdminUser user = userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
                user.setPassword(cpassword);
                userAndRoomManagementRequest.updateAdminUser(user);
                emailService.sendConfirmationEmail(email, password);
                return ResponseEntity.ok().build();
            } catch (Exception e) {     //account doesn't exist
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist or sending failed");
            }
        } else {
            try {   //find a normal user by email
                NormalUser user = userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
                user.setPassword(cpassword);
                userAndRoomManagementRequest.updateNormalUser(user);
                emailService.sendConfirmationEmail(email, password);
                return ResponseEntity.ok().build();
            } catch (Exception e) {     //account doesn't exist
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist or sending failed");
            }
        }
    }

    //to upload the new information for new user created by an admin with only the email
    @PutMapping("/newuser")
    @ResponseBody
    public ResponseEntity<User> newuser(@RequestBody SignupRequest signupRequest) {
        String cpassword=AuthUtils.encryptPassword(signupRequest.getPassword());
        if (signupRequest.getIs_admin() != null && signupRequest.getIs_admin()) {
            AdminUser user = userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(signupRequest.getEmail()));
            user.setIs_new(false);
            user.setFirst_name(signupRequest.getFirstname());
            user.setLast_name(signupRequest.getLastname());
            user.setPassword(cpassword);
            userAndRoomManagementRequest.updateAdminUser(user);
            return ResponseEntity.ok(user);
        } else {
            NormalUser user = userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(signupRequest.getEmail()));
            user.setIs_new(false);
            user.setFirst_name(signupRequest.getFirstname());
            user.setLast_name(signupRequest.getLastname());
            user.setPassword(cpassword);
            userAndRoomManagementRequest.updateNormalUser(user);
            return ResponseEntity.ok(user);
        }
    }

    //to change information, same as newuser except setIs_new
    @PutMapping("/edit")
    @ResponseBody
    public ResponseEntity<User> edit(@RequestBody SignupRequest signupRequest) {
        String cpassword=AuthUtils.encryptPassword(signupRequest.getPassword());
        if (signupRequest.getIs_admin() != null && signupRequest.getIs_admin()) {
            AdminUser user = userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(signupRequest.getEmail()));
            user.setFirst_name(signupRequest.getFirstname());
            user.setLast_name(signupRequest.getLastname());
            user.setPassword(cpassword);
            userAndRoomManagementRequest.updateAdminUser(user);
            return ResponseEntity.ok(user);
        } else {
            NormalUser user = userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(signupRequest.getEmail()));
            user.setFirst_name(signupRequest.getFirstname());
            user.setLast_name(signupRequest.getLastname());
            user.setPassword(cpassword);
            userAndRoomManagementRequest.updateNormalUser(user);
            return ResponseEntity.ok(user);
        }
    }

    @PutMapping("quitroom")
    @ResponseBody
    public ResponseEntity<?> quitroom(@RequestBody QuitRoomRequest quitRoomRequest) {
        if (quitRoomRequest.getIs_admin() != null && quitRoomRequest.getIs_admin()) {  //admin user quit a room
            userAndRoomManagementRequest.quitChatRoom(userAndRoomManagementRequest.getOneChatRoom(quitRoomRequest.getRoomId()), true, quitRoomRequest.getEmail());
            return ResponseEntity.ok().build();
        } else {  //normal user quit a room
            userAndRoomManagementRequest.quitChatRoom(userAndRoomManagementRequest.getOneChatRoom(quitRoomRequest.getRoomId()), false, quitRoomRequest.getEmail());
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("/chatroom/{roomId}")
    @ResponseBody
    public ResponseEntity<ChatRoom> getroom(@PathVariable Long roomId) {
        //to get one chatroom by id
        ChatRoom cr = userAndRoomManagementRequest.getOneChatRoom(roomId);
        return ResponseEntity.ok().body(cr);
    }

    @GetMapping("/generateImageCode")
    public void generateImageCode(HttpSession session, HttpServletResponse response) throws IOException {
        String code = VerifyCodeUtils.generateVerifyCode(4);
        session.setAttribute("code", code);
        response.setContentType("image/png");
        ServletOutputStream os = response.getOutputStream();
        VerifyCodeUtils.outputImage(130, 60, os, code);
    }
}
