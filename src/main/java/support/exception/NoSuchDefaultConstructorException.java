package support.exception;

public class NoSuchDefaultConstructorException extends RuntimeException {
    private static final String ERROR_MESSAGE = "기본 생성자를 찾지 못했습니다.";

    public NoSuchDefaultConstructorException() {
        super(ERROR_MESSAGE);
    }

    public NoSuchDefaultConstructorException(String message) {
        super(message);
    }
}
