package br.com.alura.AluraFake.repository;

import br.com.alura.AluraFake.domain.course.Course;
import br.com.alura.AluraFake.domain.task.Task;
import br.com.alura.AluraFake.domain.task.Type;
import br.com.alura.AluraFake.domain.user.Role;
import br.com.alura.AluraFake.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindByCourseId() {
        User instructor = new User();
        instructor.setName("Maria Oliveira");
        instructor.setEmail("maria.oliveira@example.com");
        instructor.setRole(Role.INSTRUCTOR);
        instructor.setPassword("123456");
        instructor = userRepository.save(instructor);

        Course course = new Course("Spring Boot Avançado", "Curso avançado de Spring Boot", instructor);
        course = courseRepository.save(course);

        Task task1 = new Task();
        task1.setCourse(course);
        task1.setStatement("Pergunta 1");
        task1.setOrder(1);
        task1.setType(Type.OPEN_TEXT);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setCourse(course);
        task2.setStatement("Pergunta 2");
        task2.setOrder(2);
        task2.setType(Type.OPEN_TEXT);
        taskRepository.save(task2);

        List<Task> tasks = taskRepository.findByCourseId(course.getId());
        assertEquals(2, tasks.size());
    }


    @Test
    @DisplayName("Deve encontrar a maior ordem de tarefa por courseId")
    void shouldFindMaxOrderByCourseId() {
        User instructor = new User();
        instructor.setName("Carlos Souza");
        instructor.setEmail("carlos.souza@example.com");
        instructor.setRole(Role.INSTRUCTOR);
        instructor.setPassword("123456");
        instructor = userRepository.save(instructor);

        Course course = new Course("Java Básico", "Curso básico de Java", instructor);
        course = courseRepository.save(course);

        Task task1 = new Task();
        task1.setCourse(course);
        task1.setStatement("Questão 1");
        task1.setOrder(3);
        task1.setType(Type.OPEN_TEXT);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setCourse(course);
        task2.setStatement("Questão 2");
        task2.setOrder(5);
        task2.setType(Type.OPEN_TEXT);
        taskRepository.save(task2);

        Optional<Integer> maxOrder = taskRepository.findMaxOrderByCourseId(course.getId());

        assertTrue(maxOrder.isPresent());
        assertEquals(5, maxOrder.get());
    }

    @Test
    @DisplayName("Deve buscar tarefas com ordem maior ou igual a um valor")
    void shouldFindByCourseIdAndOrderGreaterThanEqual() {
        User instructor = new User();
        instructor.setName("Ana Paula");
        instructor.setEmail("ana.paula@example.com");
        instructor.setRole(Role.INSTRUCTOR);
        instructor.setPassword("123456");
        instructor = userRepository.save(instructor);

        Course course = new Course("Kotlin Intermediário", "Curso intermediário de Kotlin", instructor);
        course = courseRepository.save(course);

        Task task1 = new Task();
        task1.setCourse(course);
        task1.setStatement("Q1");
        task1.setOrder(1);
        task1.setType(Type.OPEN_TEXT);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setCourse(course);
        task2.setStatement("Q2");
        task2.setOrder(3);
        task2.setType(Type.OPEN_TEXT);
        taskRepository.save(task2);

        Task task3 = new Task();
        task3.setCourse(course);
        task3.setStatement("Q3");
        task3.setOrder(5);
        task3.setType(Type.OPEN_TEXT);
        taskRepository.save(task3);

        List<Task> tasks = taskRepository.findByCourseIdAndOrderGreaterThanEqual(course.getId(), 3);
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(t -> t.getOrder() >= 3));
    }
}
