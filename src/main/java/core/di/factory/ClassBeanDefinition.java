package core.di.factory;

/**
 * @author KingCjy
 */
public class ClassBeanDefinition implements BeanDefinition {

    private Class<?> type;
    private String name;

    public ClassBeanDefinition(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ClassBeanDefinition{" +
                "type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
