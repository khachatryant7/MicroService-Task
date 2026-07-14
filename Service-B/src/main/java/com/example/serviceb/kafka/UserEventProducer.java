package com.example.serviceb.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;


    public void sendUserCreatedEvent(String email){

        String message = "User created" + email;
        log.info("[Service-a] published to Kafka: {}", message);
        kafkaTemplate.send(KafkaTopics.TOPIC3, message);

    }

}
