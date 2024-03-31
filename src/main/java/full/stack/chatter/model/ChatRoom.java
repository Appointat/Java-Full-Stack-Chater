package full.stack.chatter.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        return !this.user_list.isEmpty() ? user_list : null;
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


    public ChatRoom(){}

    public ChatRoom(String title, String description, User creator, LocalDateTime createDate,LocalDateTime expireDate, long id){
        this.title=title;
        this.description=description;
        this.creator=creator;
        this.createDate=createDate;
        this.expireDate=expireDate;
        this.id=id;
    }

    public void addUser(User user){
        if (this.user_list.isEmpty()){
            this.user_list= new ArrayList<>();
        }
        this.user_list.add(user);
    }

}
