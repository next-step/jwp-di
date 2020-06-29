package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {

    @DisplayName("Controller, Service, Repository 클래스를 모두 스캔해야한다.")
    @Test
    void scan() {
        final BeanScanner bs = new BeanScanner(this.getClass());
        assertThat(bs.loadClasses(Controller.class)).isNotNull();
        assertThat(bs.loadClasses(Service.class)).isNotNull();
        assertThat(bs.loadClasses(Repository.class)).isNotNull();
    }

    @Controller
    public static class DummyController {
        // no-op
    }

    @Service
    public static class DummyService {
        // no-op
    }

    @Repository
    public static class DummyRepository {
        // no-op
    }
}