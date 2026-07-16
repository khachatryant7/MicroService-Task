package com.example.servicea.controller;

import com.example.servicea.dto.UserDto;
import com.example.servicea.entity.UserEntity;
import com.example.servicea.integrationTest.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.kafka.listener.auto-startup=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class ServiceAControllerTest extends AbstractIntegrationTest {

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void setDown() {
        userRepository.deleteAll();
    }

    @Test
    void hello_shouldReturnGreeting() throws Exception {
        mockMvc.perform(get("/api/v1/users/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from service A!"));
    }

    @Test
    void createUser_WhenNoUsers_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/users/getUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createUser_shouldPersistUserInDatabase() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("tik");
        dto.setEmail("tik@example.com");

        mockMvc.perform(post("/api/v1/users/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("tik"))
                .andExpect(jsonPath("$.email").value("tik@example.com"));

        var saved = userRepository.findByEmail("tik@example.com");
        Assertions.assertTrue(saved.isPresent());
    }

    @Test
    void createUser_withIdempotencyKeyShouldReturnSingularUser() throws Exception {
        UserEntity singular = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Single")
                .email("single@mail.ru")
                .password("qwerty")
                .build();
        userRepository.save(singular);

        UserDto userDto = new UserDto();
        userDto.setName("Jon");
        userDto.setEmail("single@mail.ru");

        mockMvc.perform(post("/api/v1/users/createUser")
                        .header("Idempotency-key", "something-2233")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("single@mail.ru"));

        Assertions.assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void getUser_shouldReturnAllUsers() {
        userRepository.save(UserEntity.builder()
                .name("User One")
                .email("UserOne@mail.su")
                .password("qwerty")
                .build());
        userRepository.save(UserEntity.builder()
                .name("User Two")
                .email("UserTwo@mail.su")
                .password("default")
                .build());
        userRepository.save(UserEntity.builder()
                .name("User Three")
                .email("UserThree@mail.su")
                .password("1234567890")
                .build());

    }

}