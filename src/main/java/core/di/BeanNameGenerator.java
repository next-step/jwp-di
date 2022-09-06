package core.di;

import org.apache.commons.lang3.StringUtils;

public class BeanNameGenerator {

    private BeanNameGenerator() {
    }

    public static String generateBeanName(String beanClassName) {
        return StringUtils.uncapitalize(beanClassName);
    }
}
