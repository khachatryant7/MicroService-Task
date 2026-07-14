package com.example.serviceb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.kafka.listener.auto-startup=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ServiceBApplicationTests {
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Test
    void contextLoads() {
    }

}
