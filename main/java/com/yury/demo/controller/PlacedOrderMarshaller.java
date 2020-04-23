package com.yury.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yury.demo.book.Order;
import com.yury.demo.book.AcknowledgementMessage;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

//TODO refactor (combine with producerService)
@Service
public class PlacedOrderMarshaller {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    ObjectMapper mapper = new ObjectMapper();

    public PlacedOrderMarshaller() {};

    public AcknowledgementMessage marshallOrder(Order orderMessage) throws JsonProcessingException {
        /**
        orderMessage.getOrdType(),
                orderMessage.getSide(),
                orderMessage.getSymbol(),
                orderMessage.getOrderQty(),
                orderMessage.getPrice(),
                orderMessage.getClOrdID(),
                orderMessage.getChecksum()
        **/

        boolean hasErrors = false;
        if (hasErrors) {
            return new AcknowledgementMessage(AcknowledgementMessage.ResponseCode.ERROR, "Please review order");
        }

        String kafkaTopic = orderMessage.getSymbol() + "_orders3";
        placeOrderKafka(orderMessage.getClOrdID(), kafkaTopic, orderMessage);

        return new AcknowledgementMessage(AcknowledgementMessage.ResponseCode.ORDER_ACCEPTED, "Order has been placed");
    }

    public void placeOrderKafka(String key, String topic, Order order) throws JsonProcessingException {
        String JSONifiedOrder = mapper.writeValueAsString(order);
        logger.info(String.format("#### -> Producing message -> %s", JSONifiedOrder));
        this.kafkaTemplate.send(topic, key, JSONifiedOrder);
    }
}
