package core.di.factory;

import core.di.factory.example.IntegrationConfig;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConfigurationBeanScannerTest {

    @DisplayName("scan 메서드는 매개변수로 적절한 설정 클래스를 전달하면 내부의 Bean 메서드를 읽어 BeanRegister목록을 반환한다.")
    @Test
    void itIsReturnsBeanRegistersFromConfiguration() {
        ConfigurationBeanScanner beanScanner = new ConfigurationBeanScanner();
        List<BeanRegister> list = Lists.newArrayList(new ClassBeanRegister(IntegrationConfig.class));

        for (Method method : IntegrationConfig.class.getDeclaredMethods()) {
            list.add(new MethodBeanRegister(method));
        }

        Set<BeanRegister> registers = beanScanner.scan(IntegrationConfig.class);

        assertThat(registers).isNotEmpty()
                .hasSize(3)
                .containsAll(list);
    }

}
