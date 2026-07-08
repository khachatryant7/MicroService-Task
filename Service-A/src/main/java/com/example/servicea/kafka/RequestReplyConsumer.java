package com.example.servicea.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RequestReplyConsumer {

    private final Map<String, CompletableFuture<String>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<String> wait(String correlationId){
        CompletableFuture<String> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    @KafkaListener(topics = KafkaTopics.TOPIC3, groupId = "reply-group-id")
    public void onReply(String msg,
                        @Header(KafkaHeaders.RECEIVED_KEY) String correlationId) {
        log.info("[service-a] received reply from Kafka: {}", msg);
        CompletableFuture<String> future = pending.remove(correlationId);
        if (future != null) {
            future.complete(msg);
        }
    }
}