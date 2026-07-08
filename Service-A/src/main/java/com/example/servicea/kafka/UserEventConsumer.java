package com.example.servicea.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventConsumer {

    @KafkaListener(topicPattern = KafkaTopics.TOPIC, groupId = "user-service-group")
    public void consume(String msg){
        log.info("service-a published to Kafka: {}", msg);
    }
}