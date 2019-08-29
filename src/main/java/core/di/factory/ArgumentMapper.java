package core.di.factory;

@FunctionalInterface
public interface ArgumentMapper {

    Object getArgument(Class<?> clazz);

}
