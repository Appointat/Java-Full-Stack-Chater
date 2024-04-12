package full.stack.chatter.controller;


import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
public class UserController {
    @Resource
    private UserAndRoomManagementRequest userAndRoomManagementRequest;

    public UserController(UserAndRoomManagementRequest userAndRoomManagementRequest){
        this.userAndRoomManagementRequest=userAndRoomManagementRequest;
    }

    @RequestMapping("signup")
    public String signup(String first_name, String last_name, String email, String password, Boolean is_admin) {

        if (is_admin != null && is_admin) {
            AdminUser user= new AdminUser(first_name,last_name,email,password);
            userAndRoomManagementRequest.addAdminUser(user);
        }else{
            NormalUser user=new NormalUser(first_name,last_name,email,password);
            userAndRoomManagementRequest.addNormalUser(user);
        }
        return "redirect:/signin";
    }

}
