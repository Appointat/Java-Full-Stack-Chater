package full.stack.chatter.services;

import full.stack.chatter.model.AdminUser;
import full.stack.chatter.model.ChatRoom;
import full.stack.chatter.model.NormalUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Transactional
public class UserAndRoomManagementRequest {

    @PersistenceContext
    EntityManager em;

    /*
    APIs for NormalUser
     */
    public void addNormalUser(NormalUser normal_user) {
        if (isAdminUserCreated(normal_user.getEmail())) {
            throw new RuntimeException("Normal user already exists");
        }
        em.persist(normal_user);
    }

    public void updateNormalUser(NormalUser user) {
        em.merge(user);
    }

    public NormalUser getOneUser(Long id) {
        return em.find(NormalUser.class, id);
    }

    public void removeOneUser(Long id) {
        em.remove(em.find(NormalUser.class, id));
    }

    public List<NormalUser> getNormalUsers() {
        TypedQuery<NormalUser> q = em.createQuery("select nu from NormalUser nu", NormalUser.class);
        return q.getResultList();
    }

    public Boolean isNormalUserCreated(String email) {
        TypedQuery<NormalUser> q = em.createQuery("select nu from NormalUser nu where nu.email = :email", NormalUser.class);
        q.setParameter("email", email);
        return !q.getResultList().isEmpty();
    }

    public Boolean isAdminUserCreated(String email) {
        TypedQuery<AdminUser> q = em.createQuery("select au from AdminUser au where au.email = :email", AdminUser.class);
        q.setParameter("email", email);
        return !q.getResultList().isEmpty();
    }

    /*
    APIs for AdminUser
     */
    public void addAdminUser(AdminUser admin_user) {
        if (isNormalUserCreated(admin_user.getEmail())) {
            throw new RuntimeException("Admin user already exists");
        }
        em.persist(admin_user);
    }

    public AdminUser getOneAdminUser(Long id) {
        return em.find(AdminUser.class, id);
    }

    public void removeAdminUser(AdminUser admin_user) {
        em.remove(em.find(AdminUser.class, admin_user.getId()));
    }

    public void updateAdminUser(AdminUser admin_user) {
        em.merge(admin_user);
    }

    public List<AdminUser> getAdminUsers() {
        TypedQuery<AdminUser> q = em.createQuery("select au from AdminUser au", AdminUser.class);
        return q.getResultList();
    }

    /*
    APIs for ChatRoom
     */
    public void addChatRoom(ChatRoom chat_room) {
        em.persist(chat_room);
    }

    public void updateChatRoom(ChatRoom chat_room) {
        em.merge(chat_room);
    }

    public void removeChatRoom(ChatRoom chat_room) {
        em.remove(em.find(ChatRoom.class, chat_room.getId()));
    }

    public List<ChatRoom> getChatRooms() {
        TypedQuery<ChatRoom> q = em.createQuery("select cr from ChatRoom cr", ChatRoom.class);
        return q.getResultList();
    }

    public ChatRoom getOneChatRoom(Long id) {
        return em.find(ChatRoom.class, id);
    }

}