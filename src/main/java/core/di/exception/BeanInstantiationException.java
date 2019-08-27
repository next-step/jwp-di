package core.di.exception;

/**
 * @author : yusik
 * @date : 27/08/2019
 */
public class BeanInstantiationException extends RuntimeException {
    public BeanInstantiationException(Class<?> type, Throwable e) {
        super("bean instantiate exception : type=" + type.getTypeName(), e);
    }
}
