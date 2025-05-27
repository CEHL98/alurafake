package br.com.alura.AluraFake.dto;


import br.com.alura.AluraFake.dto.request.task.OptionDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OptionDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenOptionIsNull() {
        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setOption(null);
        optionDTO.setIsCorrect(true);

        Set<ConstraintViolation<OptionDTO>> violations = validator.validate(optionDTO);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("option")));
    }

    @Test
    void shouldFailWhenOptionIsTooShort() {
        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setOption("abc");
        optionDTO.setIsCorrect(true);

        Set<ConstraintViolation<OptionDTO>> violations = validator.validate(optionDTO);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("option")));
    }

    @Test
    void shouldFailWhenOptionIsTooLong() {
        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setOption("a".repeat(81));  // mais de 80 caracteres
        optionDTO.setIsCorrect(true);

        Set<ConstraintViolation<OptionDTO>> violations = validator.validate(optionDTO);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("option")));
    }

    @Test
    void shouldFailWhenIsCorrectIsNull() {
        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setOption("Opção válida");
        optionDTO.setIsCorrect(null);

        Set<ConstraintViolation<OptionDTO>> violations = validator.validate(optionDTO);
        assertFalse(violations.isEmpty());

        violations.forEach(v -> System.out.println("Violation: " + v.getPropertyPath() + " - " + v.getMessage()));

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("isCorrect")));  // ou "correct"
    }


    @Test
    void shouldPassWhenAllFieldsAreValid() {
        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setOption("Opção válida");
        optionDTO.setIsCorrect(false);

        Set<ConstraintViolation<OptionDTO>> violations = validator.validate(optionDTO);
        assertTrue(violations.isEmpty());
    }
}

