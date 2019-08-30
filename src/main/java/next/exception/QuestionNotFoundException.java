package next.exception;

import next.support.context.NotFoundException;

public class QuestionNotFoundException extends NotFoundException {

    private static final String ERROR_MESSAGE = "글을 찾을 수 없습니다.";

    public QuestionNotFoundException() {
        super(ERROR_MESSAGE);
    }
}
