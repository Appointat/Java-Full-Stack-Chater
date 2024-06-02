package full.stack.chatter.dto;

import java.time.LocalDateTime;

public class CreateroomRequest {
    private String email;
    private Boolean is_admin;
    private String title;
    private String description;
    private LocalDateTime createdate;
    private LocalDateTime expiredate;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIs_admin(Boolean is_admin) {
        this.is_admin = is_admin;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedate(LocalDateTime createdate) {
        this.createdate = createdate;
    }

    public void setExpiredate(LocalDateTime expiredate) {
        this.expiredate = expiredate;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIs_admin() {
        return is_admin;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedate() {
        return createdate;
    }

    public LocalDateTime getExpiredate() {
        return expiredate;
    }
}
