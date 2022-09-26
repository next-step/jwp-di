package core.di;

import java.util.Set;

public class BeanDefinitionRegistry {
    private final BeanDefinitions classBeanDefinitions;
    private final BeanDefinitions methodBeanDefinitions;

    public BeanDefinitionRegistry() {
        this.methodBeanDefinitions = new BeanDefinitions();
        this.classBeanDefinitions = new BeanDefinitions();
    }

    public void registerClassPathBeans(Set<BeanDefinition> classPathBeanDefinitions) {
        for (BeanDefinition classPathBeanDefinition : classPathBeanDefinitions) {
            this.classBeanDefinitions.add(classPathBeanDefinition);
        }
    }

    public void registerConfigurationBeans(Set<BeanDefinition> methodBeanDefinitions) {
        for (BeanDefinition methodBeanDefinition : methodBeanDefinitions) {
            this.methodBeanDefinitions.add(methodBeanDefinition);
        }
    }

    public BeanDefinition getMethodBeanDefinition(Class<?> methodReturnType) {
        return this.methodBeanDefinitions.getMethodBeanDefinition(methodReturnType);
    }

    public Set<BeanDefinition> getClassBeanDefinitions() {
        return this.classBeanDefinitions.getBeanDefinitions();
    }

    public Set<BeanDefinition> getMethodBeanDefinitions() {
        return this.methodBeanDefinitions.getBeanDefinitions();
    }

    public Set<Class<?>> getPreInstantiateClassBean() {
        return this.classBeanDefinitions.getPreInstantiateClassBean();
    }
}
