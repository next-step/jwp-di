package core.di.factory.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotatedBeanDefinition implements BeanDefinition {

    private final Class<?> clazz;
    private final Method method;
    private final List<Class<?>> argumentTypes;

    public AnnotatedBeanDefinition(Class<?> clazz, Method method){
        this.clazz = clazz;
        this.method = method;
        this.argumentTypes = new ArrayList<>();
        this.argumentTypes.add(this.clazz);
        this.argumentTypes.addAll(Arrays.asList(method.getParameterTypes()));
    }

    @Override
    public Class<?> getBeanType() {
        return this.method.getReturnType();
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return this.argumentTypes;
    }

    @Override
    public BeanCreator getBeanCreator(){
        return (args) -> {

            if(args.length == 0){
                throw new RuntimeException("AnnotatedBeanDefinition Error :  args size is zero");
            }

            return this.method.invoke(args[0], Arrays.copyOfRange(args, 1, args.length));
        };
    }
}
