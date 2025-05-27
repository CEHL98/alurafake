package br.com.alura.AluraFake.service;

import br.com.alura.AluraFake.domain.course.Course;
import br.com.alura.AluraFake.domain.course.Status;
import br.com.alura.AluraFake.domain.task.Task;
import br.com.alura.AluraFake.domain.task.Type;
import br.com.alura.AluraFake.domain.user.Role;
import br.com.alura.AluraFake.domain.user.User;
import br.com.alura.AluraFake.dto.request.course.NewCourseDTO;
import br.com.alura.AluraFake.dto.response.course.CourseListItemDTO;
import br.com.alura.AluraFake.exception.ApiException;
import br.com.alura.AluraFake.repository.CourseRepository;
import br.com.alura.AluraFake.repository.TaskRepository;
import br.com.alura.AluraFake.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void createCourse_shouldCreateCourseWhenInstructorExists() {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Spring Boot");
        newCourseDTO.setDescription("Curso de Spring Boot");
        newCourseDTO.setEmailInstructor("instrutor@email.com");

        User instructor = new User();
        instructor.setRole(Role.INSTRUCTOR);

        when(userRepository.findByEmail("instrutor@email.com"))
                .thenReturn(Optional.of(instructor));
        when(courseRepository.save(any(Course.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Course result = courseService.createCourse(newCourseDTO);

        assertNotNull(result);
        assertEquals("Spring Boot", result.getTitle());
        assertEquals(instructor, result.getInstructor());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_shouldThrowWhenInstructorNotFound() {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setEmailInstructor("instrutor@email.com");

        when(userRepository.findByEmail("instrutor@email.com"))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            courseService.createCourse(newCourseDTO);
        });

        assertEquals("O usuário informado não é um instrutor", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void createCourse_shouldThrowWhenUserIsNotInstructor() {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setEmailInstructor("usuario@email.com");

        User user = new User();
        user.setRole(Role.STUDENT);

        when(userRepository.findByEmail("usuario@email.com"))
                .thenReturn(Optional.of(user));

        ApiException exception = assertThrows(ApiException.class, () -> {
            courseService.createCourse(newCourseDTO);
        });

        assertEquals("O usuário informado não é um instrutor", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void listAllCourses_shouldReturnAllCourses() {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Curso 1");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Curso 2");

        when(courseRepository.findAll()).thenReturn(List.of(course1, course2));

        List<CourseListItemDTO> result = courseService.listAllCourses();

        assertEquals(2, result.size());
        assertEquals("Curso 1", result.get(0).getTitle());
        assertEquals("Curso 2", result.get(1).getTitle());
    }

    @Test
    void publishCourse_shouldPublishWhenValid() {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setStatus(Status.BUILDING);

        Task task1 = new Task();
        task1.setType(Type.OPEN_TEXT);
        task1.setOrder(1);

        Task task2 = new Task();
        task2.setType(Type.SINGLE_CHOICE);
        task2.setOrder(2);

        Task task3 = new Task();
        task3.setType(Type.MULTIPLE_CHOICE);
        task3.setOrder(3);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseId(courseId)).thenReturn(List.of(task1, task2, task3));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course result = courseService.publishCourse(courseId);

        assertEquals(Status.PUBLISHED, result.getStatus());
        assertNotNull(result.getPublishedAt());
        verify(courseRepository).save(course);
    }

    @Test
    void publishCourse_shouldThrowWhenCourseNotFound() {
        Long courseId = 999L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            courseService.publishCourse(courseId);
        });

        assertEquals("Curso não encontrado para o parâmetro informado.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void publishCourse_shouldThrowWhenStatusNotBuilding() {
        Long courseId = 1L;
        Course course = new Course();
        course.setStatus(Status.PUBLISHED);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        ApiException exception = assertThrows(ApiException.class, () -> {
            courseService.publishCourse(courseId);
        });

        assertEquals("O curso só pode ser publicado se estiver com status BUILDING", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void publishCourse_shouldThrowWhenNoTasks() {
        Long courseId = 1L;
        Course course = new Course();
        course.setStatus(Status.BUILDING);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseId(courseId)).thenReturn(List.of());

        ApiException exception = assertThrows(ApiException.class, () -> {
            courseService.publishCourse(courseId);
        });

        assertEquals("O curso não pode ser publicado sem atividades", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void publishCourse_shouldThrowWhenMissingTaskTypes() {
        Long courseId = 1L;
        Course course = new Course();
        course.setStatus(Status.BUILDING);

        Task task = new Task();
        task.setType(Type.OPEN_TEXT);
        task.setOrder(1);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseId(courseId)).thenReturn(List.of(task));

        ApiException exception = assertThrows(ApiException.class, () -> {
            courseService.publishCourse(courseId);
        });

        assertEquals("O curso deve conter pelo menos uma atividade de cada tipo", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void publishCourse_shouldThrowWhenInvalidTaskOrder() {
        Long courseId = 1L;
        Course course = new Course();
        course.setStatus(Status.BUILDING);

        Task task1 = new Task();
        task1.setType(Type.SINGLE_CHOICE);
        task1.setOrder(1);

        Task task2 = new Task();
        task2.setType(Type.MULTIPLE_CHOICE);
        task2.setOrder(3);

        Task task3 = new Task();
        task3.setType(Type.OPEN_TEXT);
        task3.setOrder(4);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseId(courseId)).thenReturn(List.of(task1, task2, task3));

        ApiException exception = assertThrows(ApiException.class, () -> {
            courseService.publishCourse(courseId);
        });

        assertEquals("As atividades devem ter orders em sequência", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}