package full.stack.chatter.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // TODO: define the id and the tittle
        long id = 1;
        String tittle = "Chat Room SR03 (" + id + ")";
        registry.addHandler(new WebSocketHandler(id, tittle), "/chatroom").setAllowedOrigins("*");
    }
}
