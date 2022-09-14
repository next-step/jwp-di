package core.di.exception;

public class ComponentScanNotFoundException extends RuntimeException {

    public ComponentScanNotFoundException() {
        super("Component Scan Value가 존재하지 않습니다.");
    }
}
