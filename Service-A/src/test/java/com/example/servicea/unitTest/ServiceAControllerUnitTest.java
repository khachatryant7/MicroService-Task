package com.example.servicea.unitTest;

import com.example.servicea.controller.ServiceAController;
import com.example.servicea.dto.UserDto;
import com.example.servicea.entity.UserEntity;
import com.example.servicea.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceAControllerUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ServiceAController controller;

    private UserEntity sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("john")
                .email("john@example.com")
                .password("qwerty")
                .build();
    }

    @Test
    void hello_returnsGreeting() {
        String result = controller.ServiceA();

        assertThat(result).isEqualTo("Hello from service A!");
    }

    @Test
    void getUsers_returnsAllUsersFromRepository() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<UserEntity> result = controller.getUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
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
                .name("jane")
                .email("jane@example.com")
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(sampleUser);

        ResponseEntity<?> response = controller.createUser(null, dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUser_withIdempotencyKey_andExistingUser_returnsExistingUser() {
        UserDto dto = UserDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));

        ResponseEntity<?> response = controller.createUser("idem-key-123", dto);

        assertThat(response.getBody()).isEqualTo(sampleUser);
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void createUser_withIdempotencyKey_andNoExistingUser_savesNewUser() {
        UserDto dto = UserDto.builder()
                .name("New User")
                .email("new@example.com")
                .build();

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(sampleUser);

        ResponseEntity<?> response = controller.createUser("idem-key-456", dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(userRepository, times(1)).findByEmail("new@example.com");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}