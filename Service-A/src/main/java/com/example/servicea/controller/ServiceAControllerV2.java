package com.example.servicea.controller;

import com.example.servicea.dto.UserDtoV2;
import com.example.servicea.dto.UserDtoV2Request;
import com.example.servicea.entity.UserEntity;
import com.example.servicea.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api")
public class ServiceAControllerV2 {

    private final UserRepository userRepository;

    public ServiceAControllerV2(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/v2/hello")
    public String helloV2() {
        return "Hello from service A! (v2)";
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

