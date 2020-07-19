package core.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by iltaek on 2020/07/16 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public interface BeanDefinition {

    Constructor<?> getBeanConstructor();

    Class<?> getBeanClass();

    Method getMethod();
}
