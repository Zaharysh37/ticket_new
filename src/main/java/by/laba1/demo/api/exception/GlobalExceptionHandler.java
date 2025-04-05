package by.laba1.demo.api.exception;

import by.laba1.demo.api.exception.throwble.BadRequestException;
import by.laba1.demo.api.exception.throwble.ConflictException;
import by.laba1.demo.api.exception.throwble.MappingException;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String ERROR_FIELD = "error";
    private static final String ERRORS_FIELD = "errors";

    // методы для формирования ответа
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(ERROR_FIELD, message));
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, Map<String, String> errors) {
        return ResponseEntity.status(status).body(Map.of(ERRORS_FIELD, errors));
    }

    // 400 Bad Request - Ошибки клиента
    /**
     * Обработка отсутствия обязательных параметров в запросе
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST,
            String.format("Missing required parameter: '%s' of type %s",
                ex.getParameterName(), ex.getParameterType()));
    }

    /**
     * Обработка кастомных исключений для некорректных запросов
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Обработка ошибок валидации DTO (@Valid аннотация)
     * Возвращает список ошибок по полям
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fieldError -> fieldError.getDefaultMessage() != null ?
                    fieldError.getDefaultMessage() : "Validation failed",
                (existing, replacement) -> existing)
            );
        return buildResponse(HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * Обработка синтаксических ошибок в JSON
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ExceptionMessage.INVALID_JSON.getMessage());
    }

    /**
     * Обработка нарушений ограничений JPA (уникальность, null-constraints и т.д.)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage)
            );
        return buildResponse(HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * Обработка несоответствия типов переданных параметров
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Parameter '%s' should be of type %s",
            ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Обработка ошибок парсинга даты/времени
     */
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, Object>> handleDateTimeParseException(DateTimeParseException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid date/time format. Expected format: " + ex.getParsedString());
    }

    // 404 Not Found - Ресурс не найден
    /**
     * Обработка случаев, когда запрашиваемый ресурс не найден
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 409 Conflict - Конфликты данных
    /**
     * Обработка нарушений целостности данных (уникальные индексы, внешние ключи)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Data conflict";
        if (ex.getMostSpecificCause() != null) {
            message += ": " + ex.getMostSpecificCause().getMessage();
        }
        return buildResponse(HttpStatus.CONFLICT, message);
    }

    /**
     * Обработка бизнес-конфликтов (кастомные проверки)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflictException(ConflictException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 500 Internal Server Error - Серверные ошибки
    /**
     * Обработка всех непредвиденных ошибок
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionMessage.UNEXPECTED_ERROR.getMessage());
    }

    /**
     * Обработка ошибок маппинга
     */
    @ExceptionHandler(MappingException.class)
    public ResponseEntity<Map<String, Object>> handleMappingException(MappingException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // Дополнительные обработчики
    /**
     * Обработка неподдерживаемых HTTP методов
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED,
            String.format("Method %s is not supported for this endpoint", ex.getMethod()));
    }
}