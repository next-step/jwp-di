package core.web.context;

import core.di.factory.BeanFactory;
import core.di.scanner.BeanScanner;

public class WebApplicationContext extends BeanFactory {

    public WebApplicationContext(Object... basePackage) {
        BeanScanner beanScanner = new BeanScanner(this, basePackage);
        beanScanner.initialize();
    }
}
