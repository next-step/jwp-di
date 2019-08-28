package core.di.factory;

import core.mvc.tobe.MyController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanScannerTest {


    @DisplayName("scan test")
    @Test
    public void scanTest(){
        BeanScanner beanScanner = new BeanScanner("core.mvc.tobe");
        Set<Class<?>> classes = beanScanner.getPreInstanticateClasses();
        assertThat(classes).isNotNull();
        assertThat(classes).isNotEmpty();
        assertThat(classes).contains(MyController.class);
    }
}
