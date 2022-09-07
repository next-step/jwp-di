package next.context.support;

import core.di.factory.BeanFactory;
import org.springframework.util.Assert;

public class GenericApplicationContext {

    protected BeanFactory beanFactory;

    public GenericApplicationContext() {
        this.beanFactory = new BeanFactory();
    }

    public GenericApplicationContext(BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

}
