package com.yury.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    @Value("${spring.ws.orderBookBroadcastTopic}")
    private String wsOrderBookBroadcastTopic;
    @Value("${spring.kafka.application.orders_topic}")
    private String kafkaOrdersTopic;
    @Value("${spring.kafka.application.fill_topic}")
    private String kafkaFillsTopic;
    @Value("${spring.kafka.application.partitions}")
    private int partitions;
    @Value("${spring.kafka.application.replicas}")
    private int replicas;

    public String getWsOrderBookBroadcastTopic() { return wsOrderBookBroadcastTopic; }
    public String getKafkaOrdersTopic() { return kafkaOrdersTopic; }
    public String getKafkaFillsTopic() { return kafkaFillsTopic; }

    @Bean
    public NewTopic orders() {
        return TopicBuilder.name(kafkaOrdersTopic)
                .partitions(partitions)
                .replicas(replicas)
                .compact()
                .build();
    }

    @Bean
    public NewTopic fill() {
        return TopicBuilder.name(kafkaFillsTopic)
                .partitions(partitions)
                .replicas(replicas)
                .compact()
                .build();
    }
}