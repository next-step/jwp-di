package core.exception;

public class BeanFactoryInitFailedException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Bean Factory 초기화에 실패했습니다. [cause: %s]";

    public BeanFactoryInitFailedException(Throwable cause) {
        super(String.format(DEFAULT_MESSAGE, cause.getMessage()));
    }
}
