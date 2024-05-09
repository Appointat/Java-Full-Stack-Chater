package full.stack.chatter.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@MappedSuperclass
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    @Column(name = "first_name")
    protected String first_name;

    @Column(name = "last_name")
    protected String last_name;

    @Column(name = "email")
    protected String email;

    @Column(name = "password")
    protected String password;

    @ElementCollection
    @CollectionTable(name = "created_chat_rooms", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "created_chat_room_id")
    protected List<Long> created_chat_rooms = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "invited_chat_rooms", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "invited_chat_room_id")
    protected List<Long> invited_chat_rooms = new ArrayList<>();

    @Column(name = "is_active")
    protected Boolean is_active;

    @Column(name = "failed_attempt")
    protected Integer failed_attempt;

    @Column(name="is_new")
    protected Boolean is_new;

    public Boolean getIs_new() {
        return is_new;
    }

    public void setIs_new(Boolean is_new) {
        this.is_new = is_new;
    }

    public int getFailed_attempt() {return failed_attempt;}

    public void setFailed_attempt(int failed_attempt) {this.failed_attempt = failed_attempt;}

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public List<Long> getCreatedChatRooms() {
        return !this.created_chat_rooms.isEmpty() ? this.created_chat_rooms : null;
    }

    public List<Long> getInvitedChatRooms() {
        return !this.invited_chat_rooms.isEmpty() ? this.invited_chat_rooms : null;
    }

    public void setFirst_name(String first_name){ this.first_name = first_name;}
    public void setLast_name(String last_name){ this.last_name = last_name;}
    public void setPassword(String password){ this.password = password;}

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

        // TODO: used for test of postgreSQL
        this.created_chat_rooms = new ArrayList<>();
        this.created_chat_rooms.add(1L);
        this.created_chat_rooms.add(2L);

        this.invited_chat_rooms = new ArrayList<>();
        this.invited_chat_rooms.add(3L);
        this.invited_chat_rooms.add(4L);
    }

    public void setIsActive(Boolean is_active) {
        this.is_active = is_active;
    }

    public void addCreatedChatRoom(Long chat_room_id) {
        if (this.created_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room already created by this user");
            return;
        }
        this.created_chat_rooms.add(chat_room_id);
    }

    public void removeCreatedChatRoom(Long chat_room_id) {
        if (!this.created_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room not created by this user");
            return;
        }
        this.created_chat_rooms.remove(chat_room_id);
    }

    public void addInvitedChatRoom(Long chat_room_id) {
        if (this.invited_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room already invited to this user");
            return;
        }
        this.invited_chat_rooms.add(chat_room_id);
    }

    public void removeInvitedChatRoom(Long chat_room_id) {
        if (!this.invited_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room not invited to this user");
            return;
        }
        this.invited_chat_rooms.remove(chat_room_id);
    }

    public void sendMsg(String msg, Long chat_room_id) { // TODO: implement this method
        Message message = new Message();
        message.setMessage(msg, this.id, chat_room_id, LocalDateTime.now());
        System.out.println("Message sent: " + msg);
    }
}
