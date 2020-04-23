package com.yury.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yury.demo.book.*;
import com.yury.demo.util.TimeStampGenerator;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class OrderBookKafkaService {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private String fillTopic = "AAPL_fill";
    private int topNLength = 10; //TODO
    private String orderBookBroadcastTopic = "/topic/greetings";

    private SortedBuyOrderArray<BuyOrder> buyOrderCache = new SortedBuyOrderArray<>();
    private SortedSellOrderArray<SellOrder> sellOrderCache = new SortedSellOrderArray<>();
    private SortedBuyOrderArray<BuyOrder> buyTopN = new SortedBuyOrderArray<>();
    private SortedSellOrderArray<SellOrder> sellTopN = new SortedSellOrderArray<>();

    @Autowired
    private TimeStampGenerator tsGenerator;
    @Autowired
    OrderBookManager orderBookManager;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "AAPL_orders3", groupId = "group_id") //TODO topic
    public void consumeOrderKafka(String message) throws IOException {
        logger.info(String.format("#### -> Consumed message -> %s", message));
        Order order = mapper.readValue(message, Order.class);

        ArrayList<Transaction> transactions = orderBookManager.processOrder(order);

        for (Transaction transaction: transactions){
            this.sendTransactionKafka(transaction);
        }

        sendOrderBookUpdateKafka();
    }

    private void sendOrderBookUpdateKafka() throws JsonProcessingException {
        SortedBuyOrderArray<BuyOrder> allBuyOrders = orderBookManager.getBuyOrders();
        SortedSellOrderArray<SellOrder> allSellOrders = orderBookManager.getSellOrders();
        Boolean arraysUnchanged = false;

        buyTopN.clear();
        sellTopN.clear();

        for (int i = 0; i < allBuyOrders.size() && i < topNLength ; i++){
            BuyOrder topI = allBuyOrders.get(i);
            buyTopN.add(topI);
            //if (topI != buyOrderCache.get(i)) { arraysUnchanged = false; } //TODO update order cache
        }
        for (int i = 0; i < allSellOrders.size() && i < topNLength ; i++){
            SellOrder topI = allSellOrders.get(i);
            sellTopN.add(topI);
            //if (topI != sellOrderCache.get(i)) { arraysUnchanged = false; }
        }
        if (!arraysUnchanged){
            String JSONifiedData = mapper.writeValueAsString(new OrderBookTopN(buyTopN, sellTopN));
            messagingTemplate.convertAndSend(orderBookBroadcastTopic, JSONifiedData);
        }

        /**
        for (int i = 0; i < allBuyOrders.size() -1 && i < topNLength -1; i++){
            allBuyOrders.get(i).setBookPosition(i);
            String JSONifiedData = mapper.writeValueAsString(allBuyOrders.get(i));
            messagingTemplate.convertAndSend(orderBookBroadcastTopic, JSONifiedData);
        }
        for (int i = 0; i < allSellOrders.size() -1 && i < topNLength -1; i++){
            allSellOrders.get(i).setBookPosition(i);
            String JSONifiedData = mapper.writeValueAsString(allSellOrders.get(i));
            messagingTemplate.convertAndSend(orderBookBroadcastTopic, JSONifiedData);
        }**///TODO
    }

    public void sendTransactionKafka(Transaction filledOrder) throws JsonProcessingException {
        String JSONifiedData = mapper.writeValueAsString(filledOrder);
        this.kafkaTemplate.send(fillTopic, tsGenerator.getCurrentTimeStamp(), JSONifiedData);
    }
}
