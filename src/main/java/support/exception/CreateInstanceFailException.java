package support.exception;

public class CreateInstanceFailException extends RuntimeException {
    private final static String ERROR_MESSAGE = "인스턴스 생성에 실패하였습니다.";

    public CreateInstanceFailException() {
        super(ERROR_MESSAGE);
    }

    public CreateInstanceFailException(String message) {
        super(message);
    }
}
