package full.stack.chatter.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "NormalUsers")
public class NormalUser extends User{
    public NormalUser(){
        super();
    }
    public NormalUser(String first_name, String last_name, String email, String password){
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.is_active = true;
        this.failed_attempt=0;
        this.is_new=false;
    }

    @ElementCollection
    @CollectionTable(name = "normal_created_chat_rooms", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "normal_created_chat_room_id")
    protected List<Long> normal_created_chat_rooms = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "normal_invited_chat_rooms", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "normal_invited_chat_room_id")
    protected List<Long> normal_invited_chat_rooms = new ArrayList<>();


    public List<Long> getCreatedChatRooms() {
        return !this.normal_created_chat_rooms.isEmpty() ? this.normal_created_chat_rooms : null;
    }

    public List<Long> getInvitedChatRooms() {
        return !this.normal_invited_chat_rooms.isEmpty() ? this.normal_invited_chat_rooms : null;
    }

    public void setUser(String first_name, String last_name, String email, String password, Boolean is_active) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.is_active = is_active;
        // TODO: used for test of postgreSQL
        this.normal_created_chat_rooms = new ArrayList<>();
        this.normal_created_chat_rooms.add(1L);
        this.normal_created_chat_rooms.add(2L);
        this.normal_invited_chat_rooms = new ArrayList<>();
        this.normal_invited_chat_rooms.add(3L);
        this.normal_invited_chat_rooms.add(4L);
    }

    //to create a chat room
    public void addCreatedChatRoom(Long chat_room_id) {
        if (this.normal_created_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room already created by this user");
            return;
        }
        this.normal_created_chat_rooms.add(chat_room_id);
    }

    //to remove a created chat room
    public void removeCreatedChatRoom(Long chat_room_id) {
        if (!this.normal_created_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room not created by this user");
            return;
        }
        this.normal_created_chat_rooms.remove(chat_room_id);
    }

    //to be invited into a chatroom
    public void addInvitedChatRoom(Long chat_room_id) {
        if (this.normal_invited_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room already invited to this user");
            return;
        }
        this.normal_invited_chat_rooms.add(chat_room_id);
    }

    //to quit a chatroom
    public void removeInvitedChatRoom(Long chat_room_id) {
        if (!this.normal_invited_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room not invited to this user");
            return;
        }
        this.normal_invited_chat_rooms.remove(chat_room_id);
    }

}
