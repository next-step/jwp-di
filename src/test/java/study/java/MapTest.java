package study.java;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Map 학습 테스트")
class MapTest {

    @DisplayName("map computeIfAbsent 학습 테스트")
    @Test
    void computeInAbsent() {
        final Map<String, Object> map = new HashMap<>();

        map.put("name", "yongju");

        final Object name = map.computeIfAbsent("name", value -> value);
        assertThat(name).isEqualTo("yongju");

        // key가 존재하면 덮어 쓰지 않는다 (put과 다름!!)
        final Object songyongju = map.computeIfAbsent("name", value -> "songyongju");
        assertThat(songyongju).isEqualTo("yongju");

        // 존재하지 않는 key는 추가하고 value 리턴한다
        final Object age = map.computeIfAbsent("age", value -> 20);
        assertThat(age).isEqualTo(20);

        assertThat(map).containsExactly(
            Map.entry("name", "yongju"),
            Map.entry("age", 20)
        );
    }

}
