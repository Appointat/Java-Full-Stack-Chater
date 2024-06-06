package full.stack.chatter.controller;

import full.stack.chatter.dto.QuitRoomRequest;
import full.stack.chatter.services.EmailService;
import full.stack.chatter.dto.CreateroomRequest;
import full.stack.chatter.dto.SignupRequest;
import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.ChatRoom;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.model.User;
import full.stack.chatter.services.EmailService;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("app")
public class AppController {

    @Resource
    private UserAndRoomManagementRequest userAndRoomManagementRequest;

    @Resource
    private final EmailService emailService;

    public AppController(UserAndRoomManagementRequest userAndRoomManagementRequest, EmailService emailService){
        this.userAndRoomManagementRequest=userAndRoomManagementRequest;
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
                    if (admin_user.getPassword().equals(password)) {        //password correct
                        admin_user.setFailed_attempt(0);                    //reset failed attempt
                        userAndRoomManagementRequest.updateAdminUser(admin_user);   //update to database
                        return ResponseEntity.ok(admin_user);               //return admin user
                    } else {                                                //password wrong
                        handlePasswordWrong(admin_user);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("password incorrect");
                    }
                }else{
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("account inactive");
                }
            } else {  //normal user
                Long normal_user_id = userAndRoomManagementRequest.findNormalUserIdByEmail(email);
                NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(normal_user_id);
                if (normal_user != null && normal_user.getIsActive()) {
                    if (normal_user.getPassword().equals(password)) {
                        normal_user.setFailed_attempt(0);
                        userAndRoomManagementRequest.updateNormalUser(normal_user);
                        return ResponseEntity.ok(normal_user);
                    } else {
                        handlePasswordWrong(normal_user);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("password incorrect");
                    }
                }else{
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("account inactive");
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not found");
        }
    }

    private void handlePasswordWrong(User user) {
        int attempts = user.getFailed_attempt() + 1;
        user.setFailed_attempt(attempts);
        if (attempts >= 3) {
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
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest){
        try {
            if (signupRequest.getIs_admin() != null && signupRequest.getIs_admin()) {
                AdminUser user = new AdminUser(signupRequest.getFirstname(), signupRequest.getLastname(), signupRequest.getEmail(), signupRequest.getPassword());
                userAndRoomManagementRequest.addAdminUser(user);
            } else {
                NormalUser user = new NormalUser(signupRequest.getFirstname(), signupRequest.getLastname(), signupRequest.getEmail(), signupRequest.getPassword());
                userAndRoomManagementRequest.addNormalUser(user);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("account existed");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @PostMapping("createroom")
    @ResponseBody
    public ResponseEntity<?> createroom(@RequestBody CreateroomRequest createroomRequest){
        ChatRoom chat_room = new ChatRoom();
        if(createroomRequest.getIs_admin()){
            AdminUser creator=userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(createroomRequest.getEmail()));
            chat_room.setChatRoom(createroomRequest.getTitle(), createroomRequest.getDescription(), creator, createroomRequest.getCreatedate(), createroomRequest.getExpiredate());
            userAndRoomManagementRequest.addChatRoom(chat_room);
            creator.addCreatedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateAdminUser(creator);
        }else{
            NormalUser creator=userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(createroomRequest.getEmail()));
            chat_room.setChatRoom(createroomRequest.getTitle(), createroomRequest.getDescription(), creator, createroomRequest.getCreatedate(), createroomRequest.getExpiredate());
            userAndRoomManagementRequest.addChatRoom(chat_room);
            creator.addCreatedChatRoom(chat_room.getId());
            userAndRoomManagementRequest.updateNormalUser(creator);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @GetMapping("createdrooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoom>> createdrooms(@RequestParam String email, @RequestParam Boolean is_admin){
        List<ChatRoom> created_rooms;
        if(is_admin){
            created_rooms=userAndRoomManagementRequest.getCreatedChatRoomsByAdminID(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
        }else{
            created_rooms=userAndRoomManagementRequest.getCreatedChatRoomsByNormalID(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
        }
        return  ResponseEntity.ok(created_rooms);
    }

    @GetMapping("invitedrooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoom>> invitedrooms(@RequestParam String email, @RequestParam Boolean is_admin){
        List<ChatRoom> invited_rooms;
        if(is_admin){
            invited_rooms=userAndRoomManagementRequest.getInvitedChatRoomsByAdminID(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
        }else{
            invited_rooms=userAndRoomManagementRequest.getInvitedChatRoomsByNormalID(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
        }
        return  ResponseEntity.ok(invited_rooms);
    }

    @DeleteMapping("/deleteroom/{roomId}")
    @ResponseBody
    public ResponseEntity<?> deleteroom(@PathVariable Long roomId){
        userAndRoomManagementRequest.removeChatRoom(userAndRoomManagementRequest.getOneChatRoom(roomId));
        return ResponseEntity.ok("Room deleted");
    }

    @PutMapping("/invite/{roomId}/{email}/{is_admin}/{invitor_email}/{invitor_admin}")
    @ResponseBody
    public ResponseEntity<?> invite(@PathVariable Long roomId, @PathVariable String email,@PathVariable Boolean is_admin,@PathVariable String invitor_email,@PathVariable Boolean invitor_admin){
        if(is_admin) {
            if (email.equals(invitor_email) && invitor_admin) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot invite yourself");
            } else {
                try {
                    AdminUser admin_user = userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
                    ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(roomId);
                    String notice = chat_room.addUser(admin_user);
                    userAndRoomManagementRequest.updateChatRoom(chat_room);
                    admin_user.addInvitedChatRoom(roomId);
                    userAndRoomManagementRequest.updateAdminUser(admin_user);
                    if (notice.equals("success")) {
                        return ResponseEntity.ok().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(notice);
                    }
                }catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
                }
            }
        }else{
            if (email.equals(invitor_email) && !invitor_admin) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot invite yourself");
            }else{
                try {
                    NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
                    ChatRoom chat_room = userAndRoomManagementRequest.getOneChatRoom(roomId);
                    String notice = chat_room.addUser(normal_user);
                    userAndRoomManagementRequest.updateChatRoom(chat_room);
                    normal_user.addInvitedChatRoom(roomId);
                    userAndRoomManagementRequest.updateNormalUser(normal_user);
                    if (notice.equals("success")) {
                        return ResponseEntity.ok().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(notice);
                    }
                }catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
                }
            }
        }
    }

    @GetMapping("/forgot")
    @ResponseBody
    public ResponseEntity<?> forgot(@RequestParam String email, @RequestParam Boolean is_admin){
        if(is_admin != null && is_admin){
            try {
                AdminUser user=userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email));
                emailService.sendConfirmationEmail(email, user.getPassword());
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist or sending failed");
            }
        }else{
            try {
                NormalUser user=userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
                emailService.sendConfirmationEmail(email, user.getPassword());
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist or sending failed");
            }
        }
    }

    @PutMapping("/newuser")
    @ResponseBody
    public ResponseEntity<User> newuser(@RequestBody SignupRequest signupRequest){
        if(signupRequest.getIs_admin() != null && signupRequest.getIs_admin()){
            AdminUser user=userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(signupRequest.getEmail()));
            user.setIs_new(false);
            user.setFirst_name(signupRequest.getFirstname());
            user.setLast_name(signupRequest.getLastname());
            user.setPassword(signupRequest.getPassword());
            userAndRoomManagementRequest.updateAdminUser(user);
            return ResponseEntity.ok(user);
        }else{
            NormalUser user=userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(signupRequest.getEmail()));
            user.setIs_new(false);
            user.setFirst_name(signupRequest.getFirstname());
            user.setLast_name(signupRequest.getLastname());
            user.setPassword(signupRequest.getPassword());
            userAndRoomManagementRequest.updateNormalUser(user);
            return ResponseEntity.ok(user);
        }
    }

    @PutMapping("/edit")
    @ResponseBody
    public ResponseEntity<User> edit(@RequestBody SignupRequest signupRequest){
        if(signupRequest.getIs_admin() != null && signupRequest.getIs_admin()){
            AdminUser user=userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(signupRequest.getEmail()));
            user.setFirst_name(signupRequest.getFirstname());
            user.setLast_name(signupRequest.getLastname());
            user.setPassword(signupRequest.getPassword());
            userAndRoomManagementRequest.updateAdminUser(user);
            return ResponseEntity.ok(user);
        }else{
            NormalUser user=userAndRoomManagementRequest.getOneNormalUser(userAndRoomManagementRequest.findNormalUserIdByEmail(signupRequest.getEmail()));
            user.setFirst_name(signupRequest.getFirstname());
            user.setLast_name(signupRequest.getLastname());
            user.setPassword(signupRequest.getPassword());
            userAndRoomManagementRequest.updateNormalUser(user);
            return ResponseEntity.ok(user);
        }
    }

    @PutMapping("quitroom")
    @ResponseBody
    public ResponseEntity<?> quitroom(@RequestBody QuitRoomRequest quitRoomRequest){
        if(quitRoomRequest.getIs_admin() != null &&quitRoomRequest.getIs_admin()){
            userAndRoomManagementRequest.quitChatRoom(userAndRoomManagementRequest.getOneChatRoom(quitRoomRequest.getRoomId()), true, quitRoomRequest.getEmail());
            return ResponseEntity.ok().build();
        }else{
            userAndRoomManagementRequest.quitChatRoom(userAndRoomManagementRequest.getOneChatRoom(quitRoomRequest.getRoomId()), false, quitRoomRequest.getEmail());
            return ResponseEntity.ok().build();
        }
    }
}
