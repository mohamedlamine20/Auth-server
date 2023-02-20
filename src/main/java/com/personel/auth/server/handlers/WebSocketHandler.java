package com.personel.auth.server.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException {
        // Handle incoming messages
        System.out.println("Text message has been sent!");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {
        // Handle new connections
        System.out.println("New connection has been established!");
    }
}
