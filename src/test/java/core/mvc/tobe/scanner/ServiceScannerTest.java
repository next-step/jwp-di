package core.mvc.tobe.scanner;

import next.service.QnaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceScannerTest {

    @DisplayName("Service 어노테이션이 있는 QnaService 클래스를 찾아서 객체로 생성한다")
    @Test
    void getServices() {
        final ServiceScanner serviceScanner = new ServiceScanner("next");
        final Map<Class<?>, Object> services = serviceScanner.getBeans();

        assertThat(services).isNotEmpty();
        assertThat(services).containsKey(QnaService.class);
    }
}