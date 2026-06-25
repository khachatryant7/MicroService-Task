package com.example.serviceb.controller;

import com.example.serviceb.dto.UserDtoV2Request;
import com.example.serviceb.entity.UserEntity;
import com.example.serviceb.kafka.UserEventProducer;
import com.example.serviceb.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceBControllerV2 {

    private final UserEventProducer userEventProducer;
    private final UserRepository userRepository;

    public ServiceBControllerV2(UserEventProducer userEventProducer, UserRepository userRepository) {
        this.userEventProducer = userEventProducer;
        this.userRepository = userRepository;
    }

    @GetMapping("/v2/hello")
    public String hello(){
        return "Hello from service B!";
    }

    @GetMapping("/v2/users")
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    @PostMapping("/v2/users")
    public ResponseEntity<?> createUser(
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
                .password("default")
                .build();
        return ResponseEntity.ok(userRepository.save(user));
    }
}
