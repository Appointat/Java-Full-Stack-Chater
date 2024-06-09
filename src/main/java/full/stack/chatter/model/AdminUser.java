package full.stack.chatter.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "AdminUsers")
public class AdminUser extends User {
    public AdminUser() {}
    public AdminUser(String first_name, String last_name, String email, String password){
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.is_active = true;
        this.failed_attempt=0;
        this.is_new=false;
    }

    public void addUserToChatRoom(User user, ChatRoom chat_room) {
        chat_room.addUser(user);
    }

    public void removeUserFromChatRoom(User user, ChatRoom chat_room) {
        chat_room.removeUser(user);
    }



    @ElementCollection
    @CollectionTable(name = "admin_created_chat_rooms", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "admin_created_chat_room_id")
    protected List<Long> admin_created_chat_rooms = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "admin_invited_chat_rooms", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "admin_invited_chat_room_id")
    protected List<Long> admin_invited_chat_rooms = new ArrayList<>();


    public List<Long> getCreatedChatRooms() {
        return !this.admin_created_chat_rooms.isEmpty() ? this.admin_created_chat_rooms : null;
    }

    public List<Long> getInvitedChatRooms() {
        return !this.admin_invited_chat_rooms.isEmpty() ? this.admin_invited_chat_rooms : null;
    }

    public void setUser(String first_name, String last_name, String email, String password, Boolean is_active) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.is_active = is_active;
    }

    //to create a chatroom
    public void addCreatedChatRoom(Long chat_room_id) {
        if (this.admin_created_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room already created by this user");
            return;
        }
        this.admin_created_chat_rooms.add(chat_room_id);
    }

    //to delete a created chatroom
    public void removeCreatedChatRoom(Long chat_room_id) {
        if (!this.admin_created_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room not created by this user");
            return;
        }
        this.admin_created_chat_rooms.remove(chat_room_id);
    }

    //to be invited into a chatroom
    public void addInvitedChatRoom(Long chat_room_id) {
        if (this.admin_invited_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room already invited to this user");
            return;
        }
        this.admin_invited_chat_rooms.add(chat_room_id);
    }

    //to quit a chatroom
    public void removeInvitedChatRoom(Long chat_room_id) {
        if (!this.admin_invited_chat_rooms.contains(chat_room_id)) {
            System.out.println("Chat room not invited to this user");
            return;
        }
        this.admin_invited_chat_rooms.remove(chat_room_id);
    }

}
