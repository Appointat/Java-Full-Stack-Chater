package full.stack.chatter.model;

import java.util.List;


public class User {

    public static class UserName {
        public String first_name;
        public String last_name;

        UserName(String first_name, String last_name) {
            this.first_name = first_name;
            this.last_name = last_name;
        }
    }

    private long id;
    private UserName name;
    private String email;
    private String password;
    private List<Long> createdChatRoom;
    private List<Long> invitedChatRoom;
    private Boolean isActive;

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return name.first_name;
    }

    public String getLastName() {
        return name.last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<Long> getCreatedChatRoom() {
        return !this.createdChatRoom.isEmpty() ? createdChatRoom : null;
    }

    public List<Long> getInvitedChatRoom() {
        return !this.invitedChatRoom.isEmpty() ? invitedChatRoom : null;
    }

    public Boolean getIsActive(){ return this.isActive; }

    public User(){}

    public User(UserName name, String email, String password, Boolean isActive, long id ){
        this.name=name;
        this.email=email;
        this.password=password;
        this.isActive=isActive;
        this.id=id;
    }
}
