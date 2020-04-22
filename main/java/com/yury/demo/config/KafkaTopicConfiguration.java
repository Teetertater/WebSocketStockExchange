package com.yury.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    @Value("${spring.kafka.application.topic}")
    private String topic;

    @Value("${spring.kafka.application.partitions}")
    private int partitions;

    @Value("${spring.kafka.application.replicas}")
    private int replicas;

    @Bean
    public NewTopic transactions() {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .compact()
                .build();
    }

    @Bean
    public NewTopic AAPL_orders() { //TODO
        return TopicBuilder.name("AAPL_orders2")
                .partitions(partitions)
                .replicas(replicas)
                .compact()
                .build();
    }

    //TODO
    @Bean
    public NewTopic AAPL_fill() {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .compact()
                .build();
    }
}