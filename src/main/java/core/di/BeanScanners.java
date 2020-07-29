package core.di;

import com.google.common.collect.Lists;
import core.di.factory.BeanFactory;

import java.util.Arrays;
import java.util.List;

public class BeanScanners {

    private List<BeanScanner<?>> scanners = Lists.newArrayList();

    public BeanScanners(BeanFactory beanFactory) {
        scanners.addAll(Arrays.asList(
                new ClasspathBeanScanner(beanFactory),
                new ConfigurationBeanScanner(beanFactory)
        ));
    }

    public void scan(Object... basePackage) {
        for (BeanScanner<?> scanner : scanners) {
            scanner.scan(basePackage);
        }
    }

}
