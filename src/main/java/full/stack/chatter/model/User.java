package full.stack.chatter.model;

import java.util.List;


public class User {
    private long id;

    public static class UserName {
        public String first_name;
        public String last_name;

        UserName(String first_name, String last_name) {
            this.first_name = first_name;
            this.last_name = last_name;
        }
    }

    private UserName name;
    private String email;
    private String password;
    private List<Long> createdChatRoom;
    private List<Long> invitedChatRoom;


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
        return createdChatRoom;
    }

    public List<Long> getInvitedChatRoom() {
        return invitedChatRoom;
    }

}
