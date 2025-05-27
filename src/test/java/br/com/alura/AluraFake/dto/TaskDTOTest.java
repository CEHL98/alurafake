package br.com.alura.AluraFake.dto;


import br.com.alura.AluraFake.dto.request.task.TaskDTO;
import br.com.alura.AluraFake.dto.request.task.OptionDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidTaskDTO() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Pergunta válida com mais de 4 caracteres");
        taskDTO.setOrder(1);

        OptionDTO option = new OptionDTO();
        option.setOption("Opção válida");
        option.setIsCorrect(true);
        taskDTO.setOptions(List.of(option));

        var violations = validator.validate(taskDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenCourseIdIsNull() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setStatement("Pergunta válida");
        taskDTO.setOrder(1);

        var violations = validator.validate(taskDTO);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenStatementIsTooShort() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("abc");
        taskDTO.setOrder(1);

        var violations = validator.validate(taskDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenOrderIsNegative() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCourseId(1L);
        taskDTO.setStatement("Pergunta válida");
        taskDTO.setOrder(-1);

        var violations = validator.validate(taskDTO);
        assertFalse(violations.isEmpty());
    }
}