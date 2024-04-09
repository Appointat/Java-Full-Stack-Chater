package full.stack.chatter.controller;

import full.stack.chatter.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    // Handle messages sent to "/app/send/message",
    // and broadcast the message to all clients subscribed to "/topic/messages"
    @MessageMapping("/send/message")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        // TODO: Save message to database
        return message; // Return message will be broadcast to all clients subscribed to "/topic/messages"
    }

}
