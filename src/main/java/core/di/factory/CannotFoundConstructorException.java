package core.di.factory;

public class CannotFoundConstructorException extends RuntimeException {
    public CannotFoundConstructorException(Throwable cause) {
        super(cause);
    }
}
