package full.stack.chatter.controller;


import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.model.User;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private UserAndRoomManagementRequest userAndRoomManagementRequest;

    public UserController(UserAndRoomManagementRequest userAndRoomManagementRequest){
        this.userAndRoomManagementRequest=userAndRoomManagementRequest;
    }

    @RequestMapping("signup")
    public String signup(String first_name, String last_name, String email, String password, Boolean admin){
        System.out.println(admin);
        if (admin != null && admin) {
            AdminUser user= new AdminUser(first_name,last_name,email,password);
            userAndRoomManagementRequest.addAdminUser(user);
            System.out.println("First Name111: " + user.getEmail());
        }else{
            NormalUser user=new NormalUser(first_name,last_name,email,password);
            userAndRoomManagementRequest.addNormalUser(user);
            System.out.println("First Name222: " + user.getEmail());
        }
        return "redirect:/signin";
    }
}
