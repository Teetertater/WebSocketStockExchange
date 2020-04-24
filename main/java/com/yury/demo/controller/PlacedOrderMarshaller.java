package com.yury.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yury.demo.book.Order;
import com.yury.demo.book.AcknowledgementMessage;
import com.yury.demo.config.KafkaTopicConfiguration;
import com.yury.demo.util.OrderValidityChecker;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlacedOrderMarshaller {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    KafkaTopicConfiguration kafkaTopicConfiguration;
    @Autowired
    private OrderValidityChecker orderValidityChecker;

    public PlacedOrderMarshaller() {}

    /**
     * Sends order to Kafka queue for processing if it is valid
     * @param orderMessage: Received order message from WebSocket
     * @return AcknowledgementMessage: Validity determined by ValidityChecker with accompanying message
     * @throws JsonProcessingException
     */
    public AcknowledgementMessage marshallOrder(Order orderMessage) throws JsonProcessingException {
        AcknowledgementMessage ack = orderValidityChecker.validOrder(orderMessage);

        if (ack.getResponseCode() == AcknowledgementMessage.ResponseCode.ORDER_ACCEPTED) {
            placeOrderKafka(orderMessage.getClOrdID(), orderMessage);
        }
        return ack;
    }

    /**
     * Converts order to JSON and sends on Kafka queue.
     * @param order: Order object
     * @throws JsonProcessingException
     */
    public void placeOrderKafka(String key, Order order) throws JsonProcessingException {
        String JSONifiedOrder = mapper.writeValueAsString(order);
        logger.info(String.format("#### -> Sending new order message -> %s", JSONifiedOrder));
        this.kafkaTemplate.send(kafkaTopicConfiguration.getKafkaOrdersTopic(), order.getClOrdID(), JSONifiedOrder);
    }
}
