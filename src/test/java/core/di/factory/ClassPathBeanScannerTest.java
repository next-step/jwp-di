package core.di.factory;

import core.mvc.tobe.MyController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassPathBeanScannerTest {


    @DisplayName("scan test")
    @Test
    public void scanTest(){
        ClassPathBeanScanner classPathBeanScanner = new ClassPathBeanScanner("core.mvc.tobe");
        Set<Class<?>> classes = classPathBeanScanner.getPreInstanticateClasses();
        assertThat(classes).isNotNull();
        assertThat(classes).isNotEmpty();
        assertThat(classes).contains(MyController.class);
    }
}
