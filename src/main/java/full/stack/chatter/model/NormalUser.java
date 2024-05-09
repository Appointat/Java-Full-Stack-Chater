package full.stack.chatter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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

}
