package com.example.serviceb.controller;

import com.example.serviceb.dto.UserDto;
import com.example.serviceb.entity.UserEntity;
import com.example.serviceb.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
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
class ServiceBControllerTest {


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("micro_service_test")
            .withUsername("root")
            .withPassword("root");

    @MockBean
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;


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
                .andExpect(content().string("Hello from service B!"));
    }

    @Test
    void createUser_WhenNoUsers_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/users/getUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getUser_shouldReturnPersistUserInDatabase() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setName("Jon Bones Jones");
        userDto.setEmail("jonjones@internet.ru");

        mockMvc.perform(post("/api/v1/users/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jon Bones Jones"))
                .andExpect(jsonPath("$.email").value("jonjones@internet.ru"));

    }

    @Test
    void createUser_withIdempotencyKeyShouldReturnSingleUser() throws Exception {

        UserEntity single = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Name One")
                .email("nameone@mail.ru")
                .password("qwerty123")
                .build();

        userRepository.save(single);

        UserDto userDto = new UserDto();
        userDto.setName("Number One");
        userDto.setEmail("numberone@gmail.com");

        mockMvc.perform(post("/api/v1/users/createUser")
                        .header("Idempotency-key", "something-223344")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("numberone@gmail.com"));

    }

    @Test
    void getUsers_shouldReturnAllUsers() {
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