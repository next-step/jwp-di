package core.di.factory.exception;

public class NoSuchDefaultConstructorException extends RuntimeException {
    public NoSuchDefaultConstructorException() {
        super();
    }

    public NoSuchDefaultConstructorException(String message) {
        super(message);
    }

    public NoSuchDefaultConstructorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchDefaultConstructorException(Throwable cause) {
        super(cause);
    }

    protected NoSuchDefaultConstructorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
