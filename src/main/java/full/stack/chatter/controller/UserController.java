package full.stack.chatter.controller;


import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
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
        try {
            if (is_admin != null && is_admin) {
                AdminUser user = new AdminUser(first_name, last_name, email, password);
                userAndRoomManagementRequest.addAdminUser(user);
            } else {
                NormalUser user = new NormalUser(first_name, last_name, email, password);
                userAndRoomManagementRequest.addNormalUser(user);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return"redirect:/signup";
        }
        return "redirect:/signin";
    }

    @RequestMapping("signin")
    public String signin(String email, String password, Boolean is_admin, HttpSession session) {
        // TODO: use try-catch
        if (is_admin != null && is_admin) {
            Long admin_user_id = userAndRoomManagementRequest.findAdminUserIdByEmail(email);
            AdminUser admin_user = userAndRoomManagementRequest.getOneAdminUser(admin_user_id);
            if (admin_user != null && admin_user.getPassword().equals(password)) {
                session.setAttribute("user", admin_user);
                return "redirect:/page_admin";
            }
            return"redirect:/signin";
        } else {
            Long normal_user_id = userAndRoomManagementRequest.findNormalUserIdByEmail(email);
            NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(normal_user_id);
            if (normal_user != null && normal_user.getPassword().equals(password)) {
                session.setAttribute("user", normal_user);
                return "redirect:/page_normaluser";
            }
            return"redirect:/signin";
        }
    }

}
