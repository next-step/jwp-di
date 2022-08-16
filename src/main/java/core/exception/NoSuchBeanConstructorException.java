package core.exception;

public class NoSuchBeanConstructorException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "해당 빈의 적절한 생성자를 찾지 못했습니다. [target: %s]";

    public NoSuchBeanConstructorException(Class<?> clazz) {
        super(String.format(DEFAULT_MESSAGE, clazz.getName()));
    }
}
