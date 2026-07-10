package com.example.serviceb.unitTest;

import com.example.serviceb.controller.ServiceBController;
import com.example.serviceb.dto.UserDto;
import com.example.serviceb.entity.UserEntity;
import com.example.serviceb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceBControllerUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    ServiceBController controller;

    private UserEntity user;

    @BeforeEach
    void setUp(){
        user = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Sergey")
                .email("Sergey@example.com")
                .password("qwerty")
                .build();
    }

    @Test
    void hello_returnsGreeting(){
        String result = controller.ServiceB();

        assertThat(result).isEqualTo("Hello from service B!");

    }

    @Test
    void getUsers_returnsAllUsersFromRepository() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserEntity> result = controller.getUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("Sergey@example.com");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUsers_returnsEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserEntity> result = controller.getUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void createUser_withoutIdempotencyKey_savesNewUser() {
        UserDto dto = UserDto.builder()
                .name("Katya")
                .email("Katya@example.com")
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        ResponseEntity<?> response = controller.createUser(null, dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUser_withIdempotencyKey_andExistingUser_returnsExistingUser() {
        UserDto dto = UserDto.builder()
                .name("Sergey")
                .email("Sergey@example.com")
                .build();

        when(userRepository.findByEmail("Sergey@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.createUser("idem-key-123", dto);

        assertThat(response.getBody()).isEqualTo(user);
        verify(userRepository, times(1)).findByEmail("Sergey@example.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void createUser_withIdempotencyKey_andNoExistingUser_savesNewUser() {
        UserDto dto = UserDto.builder()
                .name("New User")
                .email("new@example.com")
                .build();

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        ResponseEntity<?> response = controller.createUser("idem-key-456", dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(userRepository, times(1)).findByEmail("new@example.com");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}