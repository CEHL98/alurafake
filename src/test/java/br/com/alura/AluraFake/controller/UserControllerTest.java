package br.com.alura.AluraFake.controller;

import br.com.alura.AluraFake.domain.user.Role;
import br.com.alura.AluraFake.dto.request.user.NewUserDTO;
import br.com.alura.AluraFake.dto.response.user.UserListItemDTO;
import br.com.alura.AluraFake.exception.ApiException;
import br.com.alura.AluraFake.repository.UserRepository;
import br.com.alura.AluraFake.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;



    @Test
    void newUser_shouldReturnBadRequestWhenPasswordIsInvalid() throws Exception {
        NewUserDTO invalidUser = new NewUserDTO();
        invalidUser.setName("Valid Name");
        invalidUser.setEmail("valid@email.com");
        invalidUser.setRole(Role.STUDENT);
        invalidUser.setPassword("123"); // Senha inválida

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("password"))
                .andExpect(jsonPath("$[0].message").value("Password must be exactly 6 characters long if provided"));
    }

    @Test
    void listAllUsers_shouldReturnEmptyListWhenNoUsers() throws Exception {
        when(userService.listAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void newUser_shouldReturnBadRequestWithErrorItemsWhenValidationFails() throws Exception {
        NewUserDTO invalidUser = new NewUserDTO();
        invalidUser.setName(""); // Nome vazio
        invalidUser.setEmail("invalid-email");
        invalidUser.setRole(null); // Role ausente

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].message").exists())
                .andExpect(jsonPath("$[1].field").exists())
                .andExpect(jsonPath("$[1].message").exists());
    }

    private NewUserDTO createValidUserDTO() {
        NewUserDTO dto = new NewUserDTO();
        dto.setName("Valid Name");
        dto.setEmail("valid@email.com");
        dto.setRole(Role.STUDENT);
        dto.setPassword("123456");
        return dto;
    }
}