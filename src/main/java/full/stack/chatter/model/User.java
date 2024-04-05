package full.stack.chatter.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@MappedSuperclass
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Transient
    private List<Long> created_chat_rooms = new ArrayList<>();

    @Transient
    private List<Long> invited_chat_rooms = new ArrayList<>();

    @Column(name = "is_active")
    private Boolean is_active;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<Long> getCreatedChatRoom() {
        return !this.created_chat_rooms.isEmpty() ? this.created_chat_rooms : null;
    }

    public List<Long> getInvitedChatRoom() {
        return !this.invited_chat_rooms.isEmpty() ? this.invited_chat_rooms : null;
    }

    public String getFirstName() {
        return this.first_name;
    }

    public String getLastName() {
        return this.last_name;
    }

    public Boolean getIsActive() {
        return this.is_active;
    }

    public void setUser(String first_name, String last_name, String email, String password, Boolean is_active) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.is_active = is_active;
    }
}
