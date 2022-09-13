package core.di.exception;

public class BeanNotFoundException extends RuntimeException {

    public BeanNotFoundException() {
        super("의존 관계를 주입할 Bean이 없습니다.");
    }
}
