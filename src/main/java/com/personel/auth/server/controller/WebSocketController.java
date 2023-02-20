package com.personel.auth.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ws")
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/connect")
    public void connect() {
        System.out.println("/connect route has been triggered!");
        messagingTemplate.convertAndSend("/topic/connect", "Connected");
    }
    @GetMapping("/send-message")
    public String sendMessage() {
        System.out.println("/send-messages route has been triggered!");
        messagingTemplate.convertAndSend("/topic/messages", "Hello, world!");
        return "Message sent";
    }
    @SubscribeMapping("/messages")
    public String handleSubscription() {
        System.out.println("/messages route has been triggered!");
        return "Welcome to the messages channel!";
    }
}
