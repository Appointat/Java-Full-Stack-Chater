package full.stack.chatter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "AdminUsers")
public class AdminUser extends User {
    public AdminUser() {
        super();
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
