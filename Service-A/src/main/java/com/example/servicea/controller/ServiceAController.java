package com.example.servicea.controller;

import com.example.servicea.dto.UserDto;
import com.example.servicea.entity.UserEntity;
import com.example.servicea.kafka.RequestProducer;
import com.example.servicea.kafka.RequestReplyConsumer;
import com.example.servicea.kafka.UserEventProducer;
import com.example.servicea.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/users")
public class ServiceAController {

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;
    private final RequestProducer requestProducer;
    private final RequestReplyConsumer requestReplyConsumer;

    public ServiceAController(UserRepository userRepository, UserEventProducer userEventProducer, RequestProducer requestProducer, RequestReplyConsumer requestReplyConsumer) {
        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
        this.requestProducer = requestProducer;
        this.requestReplyConsumer = requestReplyConsumer;
    }

    @GetMapping("/hello")
    public String ServiceA() {
        return "Hello from service A!";
    }

    @GetMapping("/getUser")
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(
            @RequestHeader(value = "Idempotency-key", required = false) String idempotencyKey,
            @RequestBody UserDto dto) {
        if (idempotencyKey != null) {
            var existing = userRepository.findByEmail(dto.getEmail());
            if (existing.isPresent()) {
                return ResponseEntity.ok(existing.get());
            }
        }
        UserEntity user = UserEntity.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password("default")
                .build();
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping("/ask")
    public ResponseEntity<String> ask(@RequestParam String message) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = requestReplyConsumer.wait(correlationId);
        requestProducer.sendRequest(correlationId, message);
        String reply = future.get(10, TimeUnit.SECONDS);
        return ResponseEntity.ok(reply);
    }
}
