package com.example.servicea.controller;

import com.example.servicea.dto.UserDto;
import com.example.servicea.entity.UserEntity;
import com.example.servicea.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceAController {

    private final UserRepository userRepository;

    public ServiceAController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/v1/hello")
    public String ServiceA(){
        return "Hello from service A!";
    }

    @GetMapping("/v1//users")
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @RequestHeader(value = "Idempotency-key", required = false) String idempotencyKey,
            @RequestBody UserDto dto){
        if (idempotencyKey != null){
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
