package next.support.context;

import java.util.NoSuchElementException;

public class NotFoundException extends NoSuchElementException {

    public NotFoundException(final String message) {
        super(message);
    }
}
