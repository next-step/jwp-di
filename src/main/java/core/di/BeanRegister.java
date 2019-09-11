package core.di;

@FunctionalInterface
public interface BeanRegister {
    Object newInstance(Object[] parameters);
}
