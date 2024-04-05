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

    @Transient
    private User creator;

    @Transient
    private List<User> user_list = new ArrayList<>();

    @Column(name = "duration")
    private int duration;

    @Column(name = "create_date")
    private LocalDateTime create_date;

    @Column(name = "expire_date")
    private LocalDateTime expire_date;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public User getCreator() {
        return creator;
    }

    public ChatRoom() {
    }

    public int getDuration() {
        return duration;
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
        this.creator = creator;
        this.create_date = createDate;
        this.expire_date = expireDate;
        this.duration = (int) (expireDate.getSecond() - createDate.getSecond());
    }

    public void addUser(User user) {
        this.user_list.add(user);
    }

}
