package com.example.servicea.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class RequestProducer {

    KafkaTemplate<String, String> kafkaTemplate;

    public void sendRequest(String correlationId, String msg){
        log.info("[service-a] send request to: Kafka {}", msg);
        kafkaTemplate.send("request-topic", correlationId, msg);
    }

}
