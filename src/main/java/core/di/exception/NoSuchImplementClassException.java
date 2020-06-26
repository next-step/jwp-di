package core.di.exception;

public class NoSuchImplementClassException extends RuntimeException {
    public NoSuchImplementClassException(Class<?> clazz) {
        super("There is no implement class of : " + clazz.getName());
    }
}
