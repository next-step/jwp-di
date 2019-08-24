package support.exception;

@FunctionalInterface
public interface ConsumeWithException<T, E extends Exception> {
    void accept(T t) throws E;
}
