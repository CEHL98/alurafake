package br.com.alura.AluraFake.controller;

import br.com.alura.AluraFake.domain.course.Course;
import br.com.alura.AluraFake.domain.course.Status;
import br.com.alura.AluraFake.dto.request.course.NewCourseDTO;
import br.com.alura.AluraFake.dto.response.course.CourseListItemDTO;
import br.com.alura.AluraFake.exception.ApiException;
import br.com.alura.AluraFake.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    @Test
    void createCourse_shouldReturnCreatedStatus() {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Spring Boot");
        newCourseDTO.setDescription("Curso de Spring Boot");
        newCourseDTO.setEmailInstructor("instrutor@email.com");

        ResponseEntity response = courseController.createCourse(newCourseDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(courseService).createCourse(newCourseDTO);
    }

    @Test
    void listAllCourses_shouldReturnOkWithCourses() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Spring Boot");
        course.setDescription("Descrição");
        course.setStatus(Status.BUILDING);

        when(courseService.listAllCourses()).thenReturn(
                List.of(new CourseListItemDTO(course))
        );

        ResponseEntity<List<CourseListItemDTO>> response = courseController.listAllCourses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals("Spring Boot", response.getBody().get(0).getTitle());
    }

    @Test
    void listAllCourses_shouldReturnEmptyList() {
        when(courseService.listAllCourses()).thenReturn(List.of());

        ResponseEntity<List<CourseListItemDTO>> response = courseController.listAllCourses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void publishCourse_shouldReturnOk() {
        Long courseId = 1L;
        Course publishedCourse = new Course();
        publishedCourse.setId(courseId);
        publishedCourse.setStatus(Status.PUBLISHED);

        when(courseService.publishCourse(courseId)).thenReturn(publishedCourse);

        ResponseEntity response = courseController.publishCourse(courseId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(courseService).publishCourse(courseId);
    }

    @Test
    void publishCourse_shouldThrowWhenCourseNotFound() {
        Long courseId = 999L;
        when(courseService.publishCourse(courseId))
                .thenThrow(new ApiException("Curso não encontrado", HttpStatus.NOT_FOUND));

        ApiException exception = assertThrows(ApiException.class, () -> {
            courseController.publishCourse(courseId);
        });

        assertEquals("Curso não encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}