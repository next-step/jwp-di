package core.di.factory;

import java.util.function.Consumer;

public interface Subject<T> {
    void publishing(Consumer<T> action);
}
