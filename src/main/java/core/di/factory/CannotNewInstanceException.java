package core.di.factory;

public class CannotNewInstanceException extends RuntimeException {
    public CannotNewInstanceException(Throwable cause) {
        super(cause);
    }
}
