package core.di.factory;

import core.di.factory.example.NameController;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author KingCjy
 */
public class BeanScannerTest {

    private BeanFactory beanFactory;
    private BeanScanner beanScanner;

    @BeforeEach
    public void setUp() {
        beanFactory = new BeanFactory();
        beanScanner = new BeanScanner(beanFactory);
    }

    @Test
    @DisplayName("BeanFactory에 BeanDefinition등록 테스트")
    public void registerBeanDefinitionTest() {
        beanScanner.scan("core.di.factory.example");

        ClassBeanDefinition classBeanDefinition = (ClassBeanDefinition) beanFactory.getBeanDefinition(QnaController.class);

        assertThat(classBeanDefinition.getType()).isEqualTo(QnaController.class);
    }

    @Test
    @DisplayName("BeanFactory에 이름으로 BeanDefinition 등록 테스트")
    public void registerBeanDefinitionWithNameTest() {
        beanScanner.scan("core.di.factory.example");

        ClassBeanDefinition classBeanDefinition = (ClassBeanDefinition) beanFactory.getBeanDefinition(NameController.class);
        ClassBeanDefinition classBeanDefinition1 = (ClassBeanDefinition) beanFactory.getBeanDefinition("name");

        assertThat(classBeanDefinition).isNull();
        assertThat(classBeanDefinition1.getType()).isEqualTo(NameController.class);
    }

}
