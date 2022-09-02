package core.di.factory;

public interface Observer {
    void subscribe(Subject subject);

    void unsubscribe();
}
