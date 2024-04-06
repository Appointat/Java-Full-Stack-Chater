package full.stack.chatter.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ChatRooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "admin_creator")
    private AdminUser admin_creator;

    @ManyToOne
    @JoinColumn(name = "normal_creator")
    private NormalUser normal_creator;

    @Transient
    private List<User> user_list = new ArrayList<>();

    @Column(name = "duration")
    private int duration;

    @Column(name = "create_date")
    private LocalDateTime create_date;

    @Column(name = "expire_date")
    private LocalDateTime expire_date;

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public User getCreator() {
        return this.admin_creator != null ? this.admin_creator : this.normal_creator;
    }

    public ChatRoom() {
    }

    public int getDuration() {
        return this.duration;
    }

    public List<User> getUser_list() {
        return !this.user_list.isEmpty() ? this.user_list : null;
    }

    public LocalDateTime getCreateDate() {
        return this.create_date;
    }

    public LocalDateTime getExpireDate() {
        return this.expire_date;
    }

    public void setChatRoom(String title, String description, User creator, LocalDateTime createDate, LocalDateTime expireDate) {
        this.title = title;
        this.description = description;
        if (creator instanceof AdminUser) { // ensure their mutual exclusivity
            this.admin_creator = (AdminUser) creator; // safe to cast
            this.normal_creator = null;
        } else {
            this.normal_creator = (NormalUser) creator; // safe to cast
            this.admin_creator = null;
        }
        this.create_date = createDate;
        this.expire_date = expireDate;
        this.duration = (int) (expireDate.getSecond() - createDate.getSecond());
    }

    @PrePersist
    @PreUpdate
    // ChatRoom must have a creator, and it must be either an AdminUser or a NormalUser, but not both
    private void validateCreator() {
        if (this.admin_creator != null && this.normal_creator != null) {
            throw new IllegalStateException("ChatRoom cannot have both AdminUser and NormalUser as creator");
        }
    }

    public void addUser(User user) {
        this.user_list.add(user);
    }
}
