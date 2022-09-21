package core.di;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BeanDefinitionsTest {
    private BeanDefinitions beanDefinitions;
    private BeanDefinition methodBeanDefinition;
    private BeanDefinition classBeanDefinition;

    Integer testMethod() {
        return 0;
    }

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        methodBeanDefinition = new BeanDefinition(BeanDefinitionsTest.class, BeanDefinitionsTest.class.getDeclaredMethod("testMethod"));
        classBeanDefinition = new BeanDefinition(BeanDefinitionsTest.class);

        beanDefinitions = new BeanDefinitions();
        beanDefinitions.add(methodBeanDefinition);
        beanDefinitions.add(classBeanDefinition);
    }

    @Test
    @DisplayName("수동 주입으로 등록된 메서드 빈 정보를 가져온다.")
    void getMethodBeanDefinition() {
        assertThat(beanDefinitions.getMethodBeanDefinition(Integer.class)).isEqualTo(methodBeanDefinition);
    }

    @Test
    @DisplayName("자동 주입으로 등록된 클래스 빈의 타입을 가져온다.")
    void getPreInstantiateClassBean() {
        assertThat(beanDefinitions.getPreInstantiateClassBean()).containsExactly(BeanDefinitionsTest.class);
    }
}