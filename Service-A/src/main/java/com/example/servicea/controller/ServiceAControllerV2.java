package com.example.servicea.controller;

import com.example.servicea.dto.UserDtoV2;
import com.example.servicea.dto.UserDtoV2Request;
import com.example.servicea.entity.UserEntity;
import com.example.servicea.kafka.RequestProducer;
import com.example.servicea.kafka.RequestReplyConsumer;
import com.example.servicea.kafka.UserEventProducer;
import com.example.servicea.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceAControllerV2 {

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;
    private final RequestProducer requestProducer;
    private final RequestReplyConsumer requestReplyConsumer;

    public ServiceAControllerV2(UserRepository userRepository, UserEventProducer userEventProducer, RequestProducer requestProducer, RequestReplyConsumer requestReplyConsumer) {
        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
        this.requestProducer = requestProducer;
        this.requestReplyConsumer = requestReplyConsumer;
    }

    @GetMapping("/v2/hello")
    public String hello(){
        return "Hello from v2 service A!";
    }

    @GetMapping("/v2/users")
    public List<UserDtoV2> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> new UserDtoV2(u.getId(), u.getEmail(), u.getName()))
                .toList();
    }

    @PostMapping("/v2/users")
    public ResponseEntity<?> createUserV2(
            @RequestHeader(value = "Idempotency-key", required = false) String idempotencyKey,
            @RequestBody UserDtoV2Request dto) {

        if (idempotencyKey != null) {
            var existing = userRepository.findByEmail(dto.getEmail());
            if (existing.isPresent()) {
                return ResponseEntity.ok(existing.get());
            }
        }

        UserEntity user = UserEntity.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();

        return ResponseEntity.ok(userRepository.save(user));
    }
}