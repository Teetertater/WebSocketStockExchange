package com.yury.demo.controller;

import com.yury.demo.model.messages.AcknowledgementMessage;
import com.yury.demo.model.messages.NewOrderSingleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.yury.demo.model.messages.AcknowledgementMessage.ResponseCode.ORDER_ACCEPTED;

@Controller
public class WebSocketController {

    @Value("${application.timezone}")
    private String timezone;
    @Value("${application.tspattern}")
    private String timestampPattern = "yyyy-MM-dd HH:mm:ss.SSS";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampPattern);

    private PlacedOrderMarshaller placedOrderMarshaller = new PlacedOrderMarshaller();
    private final KafkaProducerService producer;
    @Autowired
    WebSocketController(KafkaProducerService producer) {
        this.producer = producer;
    }

    @MessageMapping("/transact")
    @SendTo("/topic/greetings")
    public AcknowledgementMessage greeting(@RequestBody NewOrderSingleMessage message) throws Exception {


        message.setTimestamp(ZonedDateTime.now(ZoneId.of(timezone)).format(formatter));

        AcknowledgementMessage orderAck = placedOrderMarshaller.marshallOrder(message);
        if (orderAck.getResponseCode() == ORDER_ACCEPTED) {
            String kafkaTopic = message.getSymbol() + "_orders2";
            producer.sendMessage(message.getClOrdID(), kafkaTopic, message.toString());
        }

        return orderAck;
    }
}