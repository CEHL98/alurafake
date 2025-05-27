package br.com.alura.AluraFake.repository;

import br.com.alura.AluraFake.domain.user.Role;
import br.com.alura.AluraFake.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail__should_return_existing_user() {
        User user = new User("Test User", "test@email.com", Role.STUDENT);
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("test@email.com");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test User");

        result = userRepository.findByEmail("nonexistent@email.com");
        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail__should_return_true_when_user_exists() {
        User user = new User("Test User", "test@email.com", Role.STUDENT);
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("test@email.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@email.com")).isFalse();
    }

    @Test
    void save__should_persist_user_correctly() {
        User newUser = new User("New User", "new@email.com", Role.INSTRUCTOR);

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(userRepository.existsByEmail("new@email.com")).isTrue();
    }

    @Test
    void delete__should_remove_user_from_database() {
        User user = new User("To Delete", "delete@email.com", Role.STUDENT);
        User savedUser = userRepository.save(user);

        userRepository.delete(savedUser);

        assertThat(userRepository.existsByEmail("delete@email.com")).isFalse();
    }
}