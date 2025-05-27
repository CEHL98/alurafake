package br.com.alura.AluraFake.service;

import br.com.alura.AluraFake.domain.user.User;
import br.com.alura.AluraFake.dto.request.user.NewUserDTO;
import br.com.alura.AluraFake.dto.response.user.UserListItemDTO;
import br.com.alura.AluraFake.exception.ApiException;
import br.com.alura.AluraFake.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createNewStudent(NewUserDTO newUser) {
        if(userRepository.existsByEmail(newUser.getEmail())) {
            throw new ApiException( "Email já cadastrado no sistema", HttpStatus.BAD_REQUEST);
        }
        User user = newUser.toModel();
        userRepository.save(user);
        return user;
    }

    public List<UserListItemDTO> listAllUsers(){
      return userRepository.findAll().stream().map(UserListItemDTO::new).toList();
    }


}
