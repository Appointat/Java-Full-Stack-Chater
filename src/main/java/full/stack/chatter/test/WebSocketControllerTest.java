package full.stack.chatter.test;

import full.stack.chatter.model.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketControllerTest {

    private static final String SEND_TO = "/app/send/message";
    private static final String SUBSCRIBE_TO = "/topic/messages";
    private static final int CLIENT_COUNT = 10;

    private static WebSocketStompClient stompClient;
    private static int port;
    private static List<StompSession> stompSessions = new ArrayList<>();
    private static ConcurrentLinkedQueue<Message> received_msgs = new ConcurrentLinkedQueue<>();

    @BeforeAll
    public static void setup(@Autowired WebSocketStompClient autowiredStompClient, @LocalServerPort int autowiredPort) throws InterruptedException, ExecutionException, TimeoutException {
        // The function is to set up multiple WebSocket clients to test the broadcast functionality

        stompClient = autowiredStompClient;
        port = autowiredPort;
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        CountDownLatch setupLatch = new CountDownLatch(CLIENT_COUNT);

        for (int i = 0; i < CLIENT_COUNT; i++) {
            StompSession stompSession = stompClient.connect(String.format("ws://localhost:%d/chat", port), new StompSessionHandlerAdapter() {
            }).get(5, SECONDS);
            stompSessions.add(stompSession);
            setupLatch.countDown();
        }

        setupLatch.await(10, SECONDS); // Ensure all sessions are established before proceeding with the tests
    }

    @Test
    public void testBroadcastMessage() throws Exception {
        CountDownLatch receiveLatch = new CountDownLatch(CLIENT_COUNT - 1);
        Message test_msg = new Message();
        test_msg.setMessage("Hello, World!", 1L, 1L, null);

        for (int i = 1; i < CLIENT_COUNT; i++) {
            stompSessions.get(i).subscribe(SUBSCRIBE_TO, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Message.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    if (payload instanceof Message) {
                        Message received_msg = (Message) payload;
                        received_msgs.add(received_msg);
                        receiveLatch.countDown();
                    }
                }
            });
        }

        Thread.sleep(1000); // Wait for subscriptions to complete

        stompSessions.get(0).send(SEND_TO, test_msg);
        boolean allMessagesReceived = receiveLatch.await(5, SECONDS);
        assertTrue(allMessagesReceived, "All messages should have been received by the clients");

        for (Message received_msg : received_msgs) {
            assertEquals("Hello, World!", received_msg.getMessage(), "The message content did not match");
            // Additional assertions can be added here
        }
    }


}
