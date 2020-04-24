package com.yury.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yury.demo.book.*;
import com.yury.demo.config.KafkaTopicConfiguration;
import com.yury.demo.util.TimeStampGenerator;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Responsible for:
 *  - listening for orders on Kafka queue
 *  - triggering order book update
 *  - broadcasting order book top N update to WebSocket topic
 *  - sending filled orders from order book to Kafka queue
 */
@Service
public class OrderBookCommService {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Value("${application.symbol}")
    private String symbol;
    @Value("${application.topNLength}")
    private int topNLength;

    private String orderBookCache;
    private SortedBuyOrderArray<BuyOrder> buyTopN = new SortedBuyOrderArray<>();
    private SortedSellOrderArray<SellOrder> sellTopN = new SortedSellOrderArray<>();

    @Autowired
    KafkaTopicConfiguration kafkaTopicConfiguration;
    @Autowired
    private TimeStampGenerator tsGenerator;
    @Autowired
    OrderBookManager orderBookManager;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Listens on Kafka queue for new orders. Processes newly generated transactions and updates order book top N list.
     * @param message: incoming kafka queue message
     * @throws IOException
     */
    @KafkaListener (topicPartitions =  { @TopicPartition(topic =  "#{kafkaTopicConfiguration.kafkaOrdersTopic}",
                    partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))})
    public void consumeOrderKafka(String message) throws IOException {
        logger.info(String.format("#### -> Received Order -> %s", message));
        Order order = mapper.readValue(message, Order.class);

        ArrayList<Transaction> transactions = orderBookManager.processOrder(order);

        for (Transaction transaction: transactions){
            this.sendTransactionKafka(transaction);
        }

        sendOrderBookUpdateWS();
    }

    /**
     * This method broadcasts the top N order book to a WebSocket topic
     * @throws JsonProcessingException
     */
    private void sendOrderBookUpdateWS() throws JsonProcessingException {
        SortedBuyOrderArray<BuyOrder> allBuyOrders = orderBookManager.getBuyOrders();
        SortedSellOrderArray<SellOrder> allSellOrders = orderBookManager.getSellOrders();

        buyTopN.clear();
        sellTopN.clear();

        //Obtain the top N orders from Buy/Sell books.
        for (int i = 0; i < allBuyOrders.size() && i < topNLength ; i++){
            BuyOrder topI = allBuyOrders.get(i);
            buyTopN.add(topI);
        }
        for (int i = 0; i < allSellOrders.size() && i < topNLength ; i++){
            SellOrder topI = allSellOrders.get(i);
            sellTopN.add(topI);
        }

        //Check against cache if new order book Top N is the same as previous one.
        //Comparison happens on String objects to avoid looping through arrays and copy issues.
        String JSONorderBook = mapper.writeValueAsString(new OrderBookTopN(buyTopN, sellTopN));
        if (!JSONorderBook.equals(orderBookCache)){
            messagingTemplate.convertAndSend(kafkaTopicConfiguration.getWsOrderBookBroadcastTopic(), JSONorderBook);
            orderBookCache = JSONorderBook;
            logger.info("#### -> Updated Top N order book -> %s");
        }
    }

    /**
     * Converts transaction to JSON and sends on corresponding Kafka queue.
     * @param filledOrder
     * @throws JsonProcessingException
     */
    public void sendTransactionKafka(Transaction filledOrder) throws JsonProcessingException {
        String JSONFill = mapper.writeValueAsString(filledOrder);
        String topic = kafkaTopicConfiguration.getKafkaFillsTopic();
        this.kafkaTemplate.send(topic, tsGenerator.getCurrentTimeStamp(), JSONFill);
        logger.info(String.format("#### -> Sent fill info to kafka queue -> %s", ""));
    }
}
