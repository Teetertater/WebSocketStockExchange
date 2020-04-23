package com.yury.demo.controller;

import com.yury.demo.book.Order;
import com.yury.demo.book.AcknowledgementMessage;
import com.yury.demo.util.TimeStampGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class WebSocketController {

    @Autowired
    private PlacedOrderMarshaller placedOrderMarshaller;

    @Autowired
    private TimeStampGenerator tsGenerator;

    WebSocketController(){}

    @MessageMapping("/transact")
    @SendTo("/topic/greetings")
    public void greeting(@RequestBody Order message) throws Exception {
        message.setTimestamp(tsGenerator.getCurrentTimeStamp());
        //return placedOrderMarshaller.marshallOrder(message);
        placedOrderMarshaller.marshallOrder(message);
    }
}