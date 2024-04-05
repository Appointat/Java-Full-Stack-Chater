package full.stack.chatter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "AdminUsers")
public class AdminUser extends User{
    public AdminUser(){
        super();
    }

    public void addUser(){}

    public void deleteUser(){}

    public Boolean isUserExisted(String email){
        return null;
    }

}
