package com.example.websocket;

import com.example.websocket.dto.ScenarioDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

class WebSocketApplicationTests {
//    @LocalServerPort
//    Integer port;
    String WEBSOCKET_TOPIC = "/topic/activity";

    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;

    @Test
    public void shouldReceiveAMessageFromTheServer() throws Exception {
//        String WEBSOCKET_URI = String.format("ws://localhost:%d/sock", port);
        String WEBSOCKET_URI = "ws://localhost:8080/sock";

        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));

        StompSession session = stompClient
                .connect(WEBSOCKET_URI, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/activity", new DefaultStompFrameHandler());

        String message = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"new\"\n" +
                "}";

        session.send("/app/update-scenario", message.getBytes());

        Assertions.assertEquals(message, blockingQueue.poll(1, SECONDS));
    }

    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer(new String((byte[]) o));
        }
    }

    @Test
    public void verifyWelcomeMessageIsSent() throws Exception {

        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect("ws://localhost:8080/sock", new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/activity", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ScenarioDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println(payload);
            }
        });

//        String message = "{\n" +
//                "    \"id\": 1,\n" +
//                "    \"name\": \"new\"\n" +
//                "}";

        ScenarioDto scenarioDto = new ScenarioDto();
        scenarioDto.setId(1L);
        scenarioDto.setName("new Name 2");

        session.send("/app/update-scenario", scenarioDto);

        Thread.sleep(10000);
    }

}
