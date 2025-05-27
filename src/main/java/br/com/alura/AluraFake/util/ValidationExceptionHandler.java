package br.com.alura.AluraFake.util;

import br.com.alura.AluraFake.dto.response.erro.ErrorItemDTO;
import br.com.alura.AluraFake.dto.response.erro.ErrorMessageDTO;
import br.com.alura.AluraFake.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<ErrorItemDTO>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrorItemDTO> errors = ex.getBindingResult().getFieldErrors().stream().map(ErrorItemDTO::new).toList();
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorMessageDTO> handleApiException(ApiException ex, HttpServletRequest request) {
        ErrorMessageDTO errorMessage = new ErrorMessageDTO(
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getUserMessage()
        );
        return ResponseEntity.status(ex.getStatus()).body(errorMessage);
    }
}