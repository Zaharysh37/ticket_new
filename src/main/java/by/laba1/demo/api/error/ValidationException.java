package by.laba1.demo.api.error;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
