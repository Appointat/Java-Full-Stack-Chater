package full.stack.chatter.model;

import java.util.List;

public class User {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Long> createdChatRoom;
    private List<Long> invitedChatRoom;

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
