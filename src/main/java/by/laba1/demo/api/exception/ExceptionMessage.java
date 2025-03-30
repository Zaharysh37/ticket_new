package by.laba1.demo.api.exception;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
    // Общие ошибки
    ENTITY_NOT_FOUND("Entity with ID %d not found"),
    ENTITY_WITH_CRITERIA_NOT_FOUND("No entity found matching the criteria"),
    INVALID_JSON("Invalid JSON format"),
    UNEXPECTED_ERROR("Unexpected error occurred"),
    REQUIRED_FIELD("Field '%s' is required"),
    UNIQUE_CONSTRAINT_VIOLATION("%s with this %s already exists"),

    // Ошибки сервисов
    FIELD_REQUIRED("Field is required"),
    ENTITY_HAS_NECESSARY_ENTITY("Cannot delete doctor with existing appointments"),
    NO_CONNECTION_BETWEEN("No connection between %s and %s"),

    // Ошибки валидации
    VALIDATION_FAILED("Validation failed"),
    INVALID_DATE_FORMAT("Invalid date format"),

    // Ошибки безопасности
    ACCESS_DENIED("Access denied"),
    AUTHENTICATION_FAILED("Authentication failed"),

    // Бизнес-ошибки
    DATA_CONFLICT("Data conflict"),
    OPTIMISTIC_LOCK("The resource was modified by another transaction. Please retry"),

    // Для кэша
    CACHE_KEY_GENERATION_FAILED("Failed to generate cache key"),

    // Для логов
    LOG_FILE_NOT_FOUND("Log file with date '%s' not found"),
    STATS_UNAVAILABLE("Failed to get method statistics | exception: %s");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
