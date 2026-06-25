package com.example.serviceb.controller;


import com.example.serviceb.dto.UserDto;
import com.example.serviceb.entity.UserEntity;
import com.example.serviceb.kafka.UserEventProducer;
import com.example.serviceb.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceBController {

    private final UserEventProducer userEventProducer;
    private final UserRepository userRepository;

    public ServiceBController(UserEventProducer userEventProducer, UserRepository userRepository) {
        this.userEventProducer = userEventProducer;
        this.userRepository = userRepository;
    }

    @GetMapping("/hello")
    public String ServiceB(){
        return "Hello from service B!";
    }

    @GetMapping("/users")
    public List<UserEntity> getUsers(){
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
    @RequestHeader(value = "Idempotency-key", required = false) String idempotencyKey,
    @RequestBody UserDto dto){
        if (idempotencyKey != null ){
            var existing = userRepository.findByEmail(dto.getEmail());
            if (existing.isPresent()){
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
