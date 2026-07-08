package com.example.serviceb.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendReply(String correlationId, String msg) {
        log.info("[service-b] send reply to Kafka: {}", msg);
        kafkaTemplate.send(KafkaTopics.TOPIC, correlationId, msg);
    }
}