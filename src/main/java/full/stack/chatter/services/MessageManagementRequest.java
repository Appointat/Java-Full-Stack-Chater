package full.stack.chatter.services;

import full.stack.chatter.model.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class MessageManagementRequest {
    @PersistenceContext
    EntityManager em;

    /*
    APIs for Message
     */
    public void addMessage(Message message) {
        em.persist(message);
    }

    public void updateMessage(Message message) {
        em.merge(message);
    }

    public Message getOneMessage(Long id) {
        return em.find(Message.class, id);
    }

    public void removeOneMessage(Long id) {
        // It may not be a good idea to remove a message from the database
        em.remove(em.find(Message.class, id));
    }
}
