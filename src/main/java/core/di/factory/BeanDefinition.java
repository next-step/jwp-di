/*
package core.di.factory;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class BeanDefinition {
    private final Class<?> type;
    private final Object parent;
    private final Constructor constructor;
    private final Method method;
    private final List<BeanDefinition> children = Lists.newArrayList();

    @Builder
    public BeanDefinition(
        Class<?> type,
        Object parent,
        Constructor constructor,
        Method method,
        List<BeanDefinition> children
    ) {
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException();
        }

        this.type = type;
        this.parent = parent;

        this.constructor = constructor;
        this.method = method;

        if (!CollectionUtils.isEmpty(children)) {
            this.children.addAll(children);
        }
    }

    public Set<Annotation> getAnnotations() {
        if (Objects.nonNull(method)) {
            return new HashSet<>(Arrays.asList(method.getAnnotations()));
        }

        return new HashSet<>(Arrays.asList(type.getAnnotations()));
    }
}
*/
