package core.di.exception;

public class NoDefaultConstructorException extends RuntimeException {
    public NoDefaultConstructorException(Class<?> clazz) {
        super("There is no default constructor of : " + clazz.getName());
    }
}
