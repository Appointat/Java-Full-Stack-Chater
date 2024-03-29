package full.stack.chatter.model;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoom {
    private long id;
    private String title;
    private String description;
    private User creator;
    private List<User> user_list;
    private int duration;
    private LocalDateTime createDate;
    private LocalDateTime expireDate;

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

    public List<User> getUser_list() {
        return user_list;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }
}
