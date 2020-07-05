package core.di.factory.exception;

public class BeanCurrentlyInCreationException extends RuntimeException {

    public BeanCurrentlyInCreationException(String message) {
        super(message);
    }
}
