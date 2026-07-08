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

    public void sendUserCreateEvent(String email){

        String msg = "User created" + email;
        try {
            kafkaTemplate.send(KafkaTopics.TOPIC, msg);
        } catch (RuntimeException e){
            log.error("Something {}", e.getMessage() );
        }
        log.info("[service-a] published to Kafka: {}", msg);
    }
}
