package com.yury.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class TopTenBroadcaster {
    //@Value("${}") TODO
    private String broadcast = "/topic/greetings";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //topics = "${kafka.output.topic}"
    @KafkaListener(topics = "orderbook", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessage(String msg) {
        System.out.println("Message received: " + msg);
        messagingTemplate.convertAndSend(broadcast, msg);
    }

}
