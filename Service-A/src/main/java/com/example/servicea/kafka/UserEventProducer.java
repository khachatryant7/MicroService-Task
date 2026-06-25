package com.example.servicea.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "user-events";

    public void sendUserCreateEvent(String email){

        String msg = "User created" + email;
        log.info("[service-a] published to Kafka: {}", msg);
        kafkaTemplate.send(TOPIC, msg);
    }
}
