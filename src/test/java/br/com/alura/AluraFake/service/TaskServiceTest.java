package br.com.alura.AluraFake.service;

import br.com.alura.AluraFake.domain.course.Course;
import br.com.alura.AluraFake.domain.task.Task;
import br.com.alura.AluraFake.dto.request.task.TaskDTO;
import br.com.alura.AluraFake.repository.CourseRepository;
import br.com.alura.AluraFake.domain.course.Status;
import br.com.alura.AluraFake.exception.ApiException;
import br.com.alura.AluraFake.dto.request.task.OptionDTO;
import br.com.alura.AluraFake.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Course validCourse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validCourse = new Course();
        validCourse.setId(1L);
        validCourse.setStatus(Status.BUILDING);


        when(courseRepository.findById(1L)).thenReturn(Optional.of(validCourse));
        when(taskRepository.findMaxOrderByCourseId(1L)).thenReturn(Optional.of(0));
        when(taskRepository.findByCourseId(1L)).thenReturn(List.of());
    }



    @Test
    void createOpenTextTask_Success() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Novo enunciado");
        taskDTO.setOrder(1);

        Task savedTask = new Task();
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createOpenTextTask(taskDTO);

        assertNotNull(result);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createSingleChoiceTask_Success() {
        OptionDTO option1 = new OptionDTO();
        option1.setOption("Opção A");
        option1.setIsCorrect(true);

        OptionDTO option2 = new OptionDTO();
        option2.setOption("Opção B");
        option2.setIsCorrect(false);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Novo enunciado");
        taskDTO.setOrder(1);
        taskDTO.setOptions(Arrays.asList(option1, option2));

        Task savedTask = new Task();
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createSingleChoiceTask(taskDTO);

        assertNotNull(result);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createMultipleChoiceTask_Success() {
        // Criando OptionDTOs usando setters
        OptionDTO option1 = new OptionDTO();
        option1.setOption("Opção A");
        option1.setIsCorrect(true);

        OptionDTO option2 = new OptionDTO();
        option2.setOption("Opção B");
        option2.setIsCorrect(false);

        OptionDTO option3 = new OptionDTO();
        option3.setOption("Opção C");
        option3.setIsCorrect(true);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Novo enunciado");
        taskDTO.setOrder(1);
        taskDTO.setOptions(Arrays.asList(option1, option2, option3));

        Task savedTask = new Task();
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createMultipleChoiceTask(taskDTO);

        assertNotNull(result);
        verify(taskRepository, times(1)).save(any(Task.class));
    }


    @Test
    void createTask_CourseNotFound_ShouldThrowException() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);

        ApiException exception = assertThrows(ApiException.class, () -> {
            taskService.createOpenTextTask(taskDTO);
        });

        assertEquals("Curso não encontrado ou não está no status BUILDING", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void createTask_CourseNotInBuildingStatus_ShouldThrowException() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.PUBLISHED);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);

        ApiException exception = assertThrows(ApiException.class, () -> {
            taskService.createOpenTextTask(taskDTO);
        });

        assertEquals("Curso não encontrado ou não está no status BUILDING", exception.getMessage());
    }

    @Test
    void createTask_DuplicateStatement_ShouldThrowException() {
        Task existingTask = new Task();
        existingTask.setStatement("Enunciado existente");
        when(taskRepository.findByCourseId(1L)).thenReturn(List.of(existingTask));

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Enunciado existente");

        ApiException exception = assertThrows(ApiException.class, () -> {
            taskService.createOpenTextTask(taskDTO);
        });

        assertEquals("Já existe uma atividade com esse enunciado no curso", exception.getMessage());
    }

    @Test
    void createSingleChoiceTask_InvalidOptionsCount_ShouldThrowException() {
        OptionDTO option = new OptionDTO();
        option.setOption("Única opção");
        option.setIsCorrect(true);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Novo enunciado");
        taskDTO.setOrder(1);
        taskDTO.setOptions(List.of(option));

        ApiException exception = assertThrows(ApiException.class, () -> {
            taskService.createSingleChoiceTask(taskDTO);
        });

        assertEquals("Deve haver entre 2 e 5 opções", exception.getMessage());
    }

    @Test
    void createSingleChoiceTask_NoCorrectOption_ShouldThrowException() {
        OptionDTO option1 = new OptionDTO();
        option1.setOption("Opção A");
        option1.setIsCorrect(false);

        OptionDTO option2 = new OptionDTO();
        option2.setOption("Opção B");
        option2.setIsCorrect(false);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Novo enunciado");
        taskDTO.setOrder(1);
        taskDTO.setOptions(Arrays.asList(option1, option2));

        ApiException exception = assertThrows(ApiException.class, () -> {
            taskService.createSingleChoiceTask(taskDTO);
        });

        assertEquals("Deve haver exatamente uma opção correta", exception.getMessage());
    }

    @Test
    void createMultipleChoiceTask_AllOptionsCorrect_ShouldThrowException() {
        OptionDTO option1 = new OptionDTO();
        option1.setOption("Opção A");
        option1.setIsCorrect(true);

        OptionDTO option2 = new OptionDTO();
        option2.setOption("Opção B");
        option2.setIsCorrect(true);

        OptionDTO option3 = new OptionDTO();
        option3.setOption("Opção C");
        option3.setIsCorrect(true);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Novo enunciado");
        taskDTO.setOrder(1);
        taskDTO.setOptions(Arrays.asList(option1, option2, option3));

        ApiException exception = assertThrows(ApiException.class, () -> {
            taskService.createMultipleChoiceTask(taskDTO);
        });

        assertEquals("Deve haver pelo menos uma opção incorreta", exception.getMessage());
    }

    @Test
    void createTask_InvalidOrder_ShouldThrowException() {
        when(taskRepository.findMaxOrderByCourseId(1L)).thenReturn(Optional.of(5));

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Novo enunciado");
        taskDTO.setOrder(7);

        ApiException exception = assertThrows(ApiException.class, () -> {
            taskService.createOpenTextTask(taskDTO);
        });

        assertEquals("A ordem da nova atividade deve seguir a sequência existente.", exception.getMessage());
    }

    @Test
    void createTask_OptionSameAsStatement_ShouldThrowException() {
        OptionDTO option1 = new OptionDTO();
        option1.setOption("Mesmo texto");
        option1.setIsCorrect(true);

        OptionDTO option2 = new OptionDTO();
        option2.setOption("Opção B");
        option2.setIsCorrect(false);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Mesmo texto");
        taskDTO.setOrder(1);
        taskDTO.setOptions(Arrays.asList(option1, option2));

        ApiException exception = assertThrows(ApiException.class, () -> {
            taskService.createSingleChoiceTask(taskDTO);
        });

        assertEquals("Uma opção não pode ser igual ao enunciado", exception.getMessage());
    }

    @Test
    void createTask_DuplicateOptions_ShouldThrowException() {
        OptionDTO option1 = new OptionDTO();
        option1.setOption("Opção A");
        option1.setIsCorrect(true);

        OptionDTO option2 = new OptionDTO();
        option2.setOption("opção a");
        option2.setIsCorrect(false);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Novo enunciado");
        taskDTO.setOrder(1);
        taskDTO.setOptions(Arrays.asList(option1, option2));

        ApiException exception = assertThrows(ApiException.class, () -> {
            taskService.createSingleChoiceTask(taskDTO);
        });

        assertTrue(exception.getMessage().startsWith("Existem opções duplicadas:"));
    }
}