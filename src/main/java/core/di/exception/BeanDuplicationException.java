package core.di.exception;

public class BeanDuplicationException extends RuntimeException {
    public BeanDuplicationException(Class<?> type) {
        super("There is bean duplication of " + type.getName());
    }
}
