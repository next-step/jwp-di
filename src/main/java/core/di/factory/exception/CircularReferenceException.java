package core.di.factory.exception;

public class CircularReferenceException extends IllegalArgumentException {

    public CircularReferenceException(String message) {
        super(message);
    }
}
