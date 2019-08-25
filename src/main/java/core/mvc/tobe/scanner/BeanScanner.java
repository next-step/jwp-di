package core.mvc.tobe.scanner;

import java.util.Map;

public interface BeanScanner {
    Map<Class<?>, Object> getBeans();
}
