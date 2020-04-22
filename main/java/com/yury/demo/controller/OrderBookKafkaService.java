package com.yury.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yury.demo.model.book.Order;
import com.yury.demo.model.book.OrderBookManager;
import com.yury.demo.model.book.Transaction;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderBookKafkaService {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private String fillTopic = "AAPL_fill";

    @Value("${application.timezone}")
    private String timezone;
    @Value("${application.tspattern}")
    private String timestampPattern = "yyyy-MM-dd HH:mm:ss.SSS";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampPattern);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    OrderBookManager orderBookManager = OrderBookManager.getInstance();
    ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "AAPL_orders2", groupId = "group_id")
    public void consumeOrderKafka(String message) throws IOException {
        logger.info(String.format("#### -> Consumed message -> %s", message));
        Order order = mapper.readValue(message, Order.class);
        orderBookManager.processOrder(order);
/**        for (Transaction transaction: orderBookManager.processOrder(order)){
            this.sendTransactionKafka(transaction);
        }**/
    }

    public void sendTransactionKafka(Transaction filledOrder) throws JsonProcessingException {
        logger.info(String.format("#### -> Producing message -> %s", filledOrder));

        String JSONifiedData = mapper.writeValueAsString(filledOrder);
        String timeStamp = ZonedDateTime.now(ZoneId.of(timezone)).format(formatter);
        this.kafkaTemplate.send(fillTopic, timeStamp, JSONifiedData);
    }

}
