package full.stack.chatter.services;


import full.stack.chatter.model.NormalUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class ServicesRequest {

    @PersistenceContext
    EntityManager em;

    public void addUser(NormalUser normal_user) {
        em.persist(normal_user);
    }

    public void updateUser(NormalUser user) {
        em.merge(user);
    }

    public NormalUser getOneUser(int id) {
        //return un user via la clé primaire
        return em.find(NormalUser.class, id);
    }

    public void deleteOneUser(int id) {
        //return un user via la clé primaire
        em.remove(em.find(NormalUser.class, id));
    }

    public List<NormalUser> getNormalUsers() {
        Query q = em.createQuery("select nu from NormalUser nu");
        return q.getResultList();
    }


}
