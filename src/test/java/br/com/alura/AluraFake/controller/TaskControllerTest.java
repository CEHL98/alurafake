package br.com.alura.AluraFake.controller;

import br.com.alura.AluraFake.domain.task.Task;
import br.com.alura.AluraFake.dto.request.task.OptionDTO;
import br.com.alura.AluraFake.dto.request.task.TaskDTO;
import br.com.alura.AluraFake.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @Test
    void newOpenTextExercise_shouldCreateTaskAndReturnOk() {
        TaskDTO taskDTO = createValidTaskDTO();
        Task expectedTask = new Task();

        when(taskService.createOpenTextTask(taskDTO)).thenReturn(expectedTask);

        ResponseEntity<Task> response = taskController.newOpenTextExercise(taskDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(expectedTask, response.getBody());
        verify(taskService).createOpenTextTask(taskDTO);
    }

    @Test
    void newSingleChoice_shouldCreateTaskAndReturnOk() {
        TaskDTO taskDTO = createValidTaskDTO();
        taskDTO.setOptions(List.of(createOptionDTO("Option 1", true)));
        Task expectedTask = new Task();

        when(taskService.createSingleChoiceTask(taskDTO)).thenReturn(expectedTask);

        ResponseEntity<Task> response = taskController.newSingleChoice(taskDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(expectedTask, response.getBody());
        verify(taskService).createSingleChoiceTask(taskDTO);
    }

    @Test
    void newMultipleChoice_shouldCreateTaskAndReturnOk() {
        TaskDTO taskDTO = createValidTaskDTO();
        taskDTO.setOptions(List.of(
                createOptionDTO("Option 1", true),
                createOptionDTO("Option 2", false)
        ));
        Task expectedTask = new Task();

        when(taskService.createMultipleChoiceTask(taskDTO)).thenReturn(expectedTask);

        ResponseEntity<Task> response = taskController.newMultipleChoice(taskDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(expectedTask, response.getBody());
        verify(taskService).createMultipleChoiceTask(taskDTO);
    }

    private TaskDTO createValidTaskDTO() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Test statement");
        taskDTO.setOrder(1);
        return taskDTO;
    }

    private OptionDTO createOptionDTO(String option, boolean isCorrect) {
        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setOption(option);
        optionDTO.setIsCorrect(isCorrect);
        return optionDTO;
    }
}