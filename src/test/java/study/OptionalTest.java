package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptionalTest {

    private boolean isRunning = false;
    private static final String DEFAULT = "value";
    private static final String ELSE_VALUE = "else-value";

    @DisplayName("optional mapping 중 null 일 때 orElse 의 elseValue() 실행 됨")
    @Test
    void orElseWithNull() {
        final String nullValue = Optional.ofNullable(DEFAULT)
                .map(this::getNull)
                .orElse(elseValue());

        assertThat(nullValue).isEqualTo(ELSE_VALUE);
        assertTrue(isRunning);
    }

    @DisplayName("optional mapping 중 null 이 아닐 때 orElse 의 elseValue() 실행 됨")
    @Test
    void orElseWithNotNull() {
        final String notNullValue = Optional.ofNullable(DEFAULT)
                .map(this::getNotNull)
                .orElse(elseValue());

        assertThat(notNullValue).isEqualTo(DEFAULT);
        assertTrue(isRunning);
    }

    @DisplayName("optional mapping 중 null 일 때 orElseGet 의 elseValue() 실행 됨")
    @Test
    void orElseGetWithNull() {
        final String nullValue = Optional.ofNullable(DEFAULT)
                .map(this::getNull)
                .orElseGet(this::elseValue);

        assertThat(nullValue).isEqualTo(ELSE_VALUE);
        assertTrue(isRunning);
    }

    @DisplayName("optional mapping 중 null 이 아닐때 orElseGet 의 elseValue() 실행 안됨")
    @Test
    void orElseGetWithNotNull() {
        final String notNullValue = Optional.ofNullable(DEFAULT)
                .map(this::getNotNull)
                .orElseGet(this::elseValue);

        assertThat(notNullValue).isEqualTo(DEFAULT);
        assertFalse(isRunning);
    }

    String getNotNull(String src) {
        return src;
    }

    String getNull(String src) {
        return null;
    }

    String elseValue() {
        isRunning = true;
        return ELSE_VALUE;
    }

}
