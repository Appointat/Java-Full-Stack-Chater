package full.stack.chatter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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

    public Boolean getIsUserExisted(String email) { // TODO: implement this method
        return null;
    }

}
