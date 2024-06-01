package full.stack.chatter.controller;


import full.stack.chatter.dto.SignupRequest;
import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.model.User;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("app")
public class AppController {

    @Autowired
    private UserAndRoomManagementRequest userAndRoomManagementRequest;


    @GetMapping("signin")
    @ResponseBody
    public ResponseEntity<?> signin(@RequestParam String email, @RequestParam String password, @RequestParam Boolean is_admin) {
        try {
            System.out.println("111");
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
                        System.out.println("123");
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





}
