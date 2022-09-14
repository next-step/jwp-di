package core.di.exception;

public class BeanCreateFailException extends RuntimeException {

    public BeanCreateFailException() {
        super("빈 추가에 실패 하였습니다.");
    }

    public BeanCreateFailException(Exception e) {
        super("빈 추가에 실패 하였습니다. Error Message : " + e);
    }
}
