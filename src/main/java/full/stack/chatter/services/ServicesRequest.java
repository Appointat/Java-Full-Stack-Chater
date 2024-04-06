package full.stack.chatter.services;


import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.ChatRoom;
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

    /*
    APIs for NormalUser
     */
    public void addNormalUser(NormalUser normal_user) {
        em.persist(normal_user);
    }

    public void updateUser(NormalUser user) {
        // TODO
        em.merge(user);
    }

    public NormalUser getOneUser(int id) {
        // TODO
        //return un user via la clé primaire
        return em.find(NormalUser.class, id);
    }

    public void deleteOneUser(int id) {
        // TODO
        //return un user via la clé primaire
        em.remove(em.find(NormalUser.class, id));
    }

    public List<NormalUser> getNormalUsers() {
        Query q = em.createQuery("select nu from NormalUser nu");
        return q.getResultList();
    }

    /*
    APIs for AdminUser
     */
    public void addAdminUser(AdminUser admin_user) {
        em.persist(admin_user);
    }

    public void deleteAdminUser(NormalUser normal_user) {
        // TODO
        em.remove(normal_user);
    }

    public List<AdminUser> getAdminUsers() {
        Query q = em.createQuery("select au from AdminUser au");
        return q.getResultList();
    }

    /*
    APIs for ChatRoom
     */
    public void addChatRoom(ChatRoom chat_room) {
        em.persist(chat_room);
    }

    public List<ChatRoom> getChatRooms() {
        Query q = em.createQuery("select cr from ChatRoom cr");
        return q.getResultList();
    }
}