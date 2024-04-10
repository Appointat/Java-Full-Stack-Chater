package full.stack.chatter.controller;

import full.stack.chatter.model.Message;
import full.stack.chatter.services.MessageManagementRequest;
import jakarta.annotation.Resource;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @Resource
    private MessageManagementRequest messageManagementRequest;

    // Handle messages sent to "/app/send/message",
    // and broadcast the message to all clients subscribed to "/topic/messages"
    @MessageMapping(value = "/send/message")
    @SendTo(value = "/topic/messages")
    public Message sendMessage(Message message) {
        // Save message to database
        System.out.println(message.getMessage());
        messageManagementRequest.addMessage(message);
        return message; // Return message will be broadcast to all clients subscribed to "/topic/messages"
    }

}
