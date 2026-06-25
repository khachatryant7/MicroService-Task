package com.example.serviceb.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;

@Slf4j
@AllArgsConstructor
@Service
public class RequestConsumer {

    private ReplyProducer replyProducer;

    @KafkaListener(topics = "request-topic" , groupId = "request-group-id")
    public void onRequest(String msg,
                          @Header(KafkaHeaders.RECEIVED_KEY) String correlationId){
        log.info("[Service-b] received request: {}", msg);
        String reply = "Hello from service B! You asked" + msg;
        replyProducer.sendReply(correlationId, msg);
    }

}
