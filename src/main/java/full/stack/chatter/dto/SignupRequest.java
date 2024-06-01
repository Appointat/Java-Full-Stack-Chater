package full.stack.chatter.dto;

public class SignupRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Boolean is_admin;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getIs_admin() {
        return is_admin;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIs_admin(Boolean is_admin) {
        this.is_admin = is_admin;
    }
}
