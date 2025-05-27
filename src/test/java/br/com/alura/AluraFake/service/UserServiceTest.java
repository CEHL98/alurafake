package br.com.alura.AluraFake.service;


import br.com.alura.AluraFake.domain.user.Role;
import br.com.alura.AluraFake.domain.user.User;
import br.com.alura.AluraFake.dto.request.user.NewUserDTO;
import br.com.alura.AluraFake.dto.response.user.UserListItemDTO;
import br.com.alura.AluraFake.exception.ApiException;
import br.com.alura.AluraFake.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createNewStudent_shouldCreateUserWhenEmailNotExists() {
        NewUserDTO newUserDTO = createValidUserDTO();
        User expectedUser = newUserDTO.toModel();

        when(userRepository.existsByEmail(newUserDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        User result = userService.createNewStudent(newUserDTO);

        assertNotNull(result);
        assertEquals(newUserDTO.getName(), result.getName());
        assertEquals(newUserDTO.getEmail(), result.getEmail());
        assertEquals(newUserDTO.getRole(), result.getRole());

        verify(userRepository).existsByEmail(newUserDTO.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createNewStudent_shouldThrowExceptionWhenEmailExists() {
        NewUserDTO newUserDTO = createValidUserDTO();

        when(userRepository.existsByEmail(newUserDTO.getEmail())).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.createNewStudent(newUserDTO);
        });

        assertEquals("Email já cadastrado no sistema", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(userRepository).existsByEmail(newUserDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void listAllUsers_shouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserListItemDTO> result = userService.listAllUsers();

        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void listAllUsers_shouldReturnListOfUsers() {
        User user1 = new User("User 1", "user1@test.com", Role.STUDENT);
        User user2 = new User("User 2", "user2@test.com", Role.INSTRUCTOR);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserListItemDTO> result = userService.listAllUsers();

        assertEquals(2, result.size());

        assertEquals("User 1", result.get(0).getName());
        assertEquals("user1@test.com", result.get(0).getEmail());
        assertEquals(Role.STUDENT, result.get(0).getRole());

        assertEquals("User 2", result.get(1).getName());
        assertEquals(Role.INSTRUCTOR, result.get(1).getRole());

        verify(userRepository).findAll();
    }

    private NewUserDTO createValidUserDTO() {
        NewUserDTO dto = new NewUserDTO();
        dto.setName("John Doe");
        dto.setEmail("john@email.com");
        dto.setRole(Role.STUDENT);
        dto.setPassword("123456");
        return dto;
    }
}