package full.stack.chatter.controller;


import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.NormalUser;
import full.stack.chatter.services.UserAndRoomManagementRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.List;

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
        if (is_admin != null && is_admin) { // if the user is an admin
            Long admin_user_id = userAndRoomManagementRequest.findAdminUserIdByEmail(email);
            AdminUser admin_user = userAndRoomManagementRequest.getOneAdminUser(admin_user_id);

            if (admin_user != null && admin_user.getPassword().equals(password)) { // if the password is correct
                session.setAttribute("user", admin_user);
                return "redirect:/page_admin";
            }
            return"redirect:/signin";
        } else { // if the user is a normal user
            Long normal_user_id = userAndRoomManagementRequest.findNormalUserIdByEmail(email);
            NormalUser normal_user = userAndRoomManagementRequest.getOneNormalUser(normal_user_id);

            if (normal_user != null && normal_user.getPassword().equals(password)) { // if the password is correct
                session.setAttribute("user", normal_user);
                return "redirect:/page_normaluser";
            }
            return"redirect:/signin";
        }
    }

    @RequestMapping("userlist")
    public String userlist(Model model){
        List<NormalUser> normalUserList=userAndRoomManagementRequest.getNormalUsers();
        List<AdminUser> adminUserList=userAndRoomManagementRequest.getAdminUsers();

        model.addAttribute("normalUserList", normalUserList);
        model.addAttribute("adminUserList", adminUserList);
        return "page_userlist";
    }

    @RequestMapping("normaldelete")
    public String normaldelete(String email){
        userAndRoomManagementRequest.removeOneUser(userAndRoomManagementRequest.findNormalUserIdByEmail(email));
        return "redirect:/user/userlist";
    }
    @RequestMapping("admindelete")
    public String admindelete(String email){
        userAndRoomManagementRequest.removeAdminUser(userAndRoomManagementRequest.getOneAdminUser(userAndRoomManagementRequest.findAdminUserIdByEmail(email)));
        return "redirect:/user/userlist";
    }
}
