package com.yury.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yury.demo.book.Order;
import com.yury.demo.book.OrderBookManager;
import com.yury.demo.book.Transaction;
import com.yury.demo.util.TimeStampGenerator;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OrderBookKafkaService {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private String fillTopic = "AAPL_fill";

    @Autowired
    private TimeStampGenerator tsGenerator;

    @Autowired
    OrderBookManager orderBookManager;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "AAPL_orders3", groupId = "group_id") //TODO topic
    public void consumeOrderKafka(String message) throws IOException {
        logger.info(String.format("#### -> Consumed message -> %s", message));
        Order order = mapper.readValue(message, Order.class);

        for (Transaction transaction: orderBookManager.processOrder(order)){
            this.sendTransactionKafka(transaction);
        }
    }

    public void sendTransactionKafka(Transaction filledOrder) throws JsonProcessingException {
        logger.info(String.format("#### -> Producing message -> %s", filledOrder));

        String JSONifiedData = mapper.writeValueAsString(filledOrder);
        this.kafkaTemplate.send(fillTopic, tsGenerator.getCurrentTimeStamp(), JSONifiedData);
    }

}
