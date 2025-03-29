package by.laba1.demo.api.exception.throwble;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}