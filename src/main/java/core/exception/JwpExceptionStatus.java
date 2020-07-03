package core.exception;

import lombok.Getter;

@Getter
public enum JwpExceptionStatus {
    CONSTRUCTOR_NEW_INSTANCE_FAIL("constructor new Instance fail");

    private String message;

    JwpExceptionStatus(String message) {
        this.message = message;
    }
}
