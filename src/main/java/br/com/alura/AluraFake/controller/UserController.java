package br.com.alura.AluraFake.controller;

import br.com.alura.AluraFake.dto.request.user.NewUserDTO;
import br.com.alura.AluraFake.domain.user.User;
import br.com.alura.AluraFake.dto.response.user.UserListItemDTO;
import br.com.alura.AluraFake.repository.UserRepository;
import br.com.alura.AluraFake.dto.response.erro.ErrorItemDTO;
import br.com.alura.AluraFake.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
    }

    @Transactional
    @PostMapping("/user/new")
    public ResponseEntity newStudent(@RequestBody @Valid NewUserDTO newUser) {
        userService.createNewStudent(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<UserListItemDTO>> listAllUsers() {
        return ResponseEntity.ok(userService.listAllUsers());
    }

}
