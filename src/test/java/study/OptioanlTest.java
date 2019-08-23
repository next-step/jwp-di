package study;

import org.junit.jupiter.api.Test;

import java.util.Optional;

public class OptioanlTest {

    @Test
    void optional() {
        final String value = Optional.ofNullable("abc")
                .map(this::getNotNull)
                .orElse( newString());

        System.out.println(value);
    }

    String getNotNull(String src) {
        return src;
    }

    String getNull(String src) {
        return null;
    }

    String newString() {
        return "newString";
    }

}
