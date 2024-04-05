package full.stack.chatter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "NormalUsers")
public class NormalUser extends User{
    public NormalUser(){
        super();
    }
}
