package by.laba1.demo.api.exception.throwble;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String message) {
        super(message);
    }
}