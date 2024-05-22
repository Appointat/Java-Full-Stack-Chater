package full.stack.chatter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig  implements WebMvcConfigurer {


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("page_signin");
        registry.addViewController("signin").setViewName("page_signin");
        registry.addViewController("signup").setViewName("page_signup");
        registry.addViewController("page_normaluser").setViewName("page_normaluser");
        registry.addViewController("page_admin").setViewName("page_admin");
        registry.addViewController("page_admin_create").setViewName("page_admin_create");
        registry.addViewController("page_first_login").setViewName("page_first_login");
        registry.addViewController("page_edit").setViewName("page_edit");
        registry.addViewController("forget").setViewName("page_forget");
    }
}
