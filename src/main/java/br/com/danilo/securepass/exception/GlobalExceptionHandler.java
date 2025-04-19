package br.com.danilo.securepass.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

/**
 * Classe responsável por capturar e tratar globalmente as exceções lançadas durante a execução da aplicação.
 * Fornece respostas customizadas e mais compreensíveis para o usuário (ex: mensagens de validação, erros genéricos, etc).
 * Utiliza a anotação @RestControllerAdvice para aplicar o tratamento de erros em todos os controllers da aplicação.
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura exceções de validação causadas por campos inválidos nas requisições
     * com @Valid e retorna uma lista de erros contendo o campo e a mensagem associada.
     *
     * Exemplo de resposta:
     * {
     *   "errors": [
     *     { "field": "password", "message": "A senha não pode ser nula." },
     *     { "field": "name", "message": "O nome não deve estar em branco." }
     *   ]
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Erro na validação dos dados: {}", ex.getMessage(), ex);

        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", Objects.requireNonNull(error.getDefaultMessage())
                ))
                .toList();

        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Erro de integridade de dados: {}", ex.getMessage(), ex);

        String message = "Esse nome de usuário já está em uso. Por favor, escolha outro.";

        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

}
