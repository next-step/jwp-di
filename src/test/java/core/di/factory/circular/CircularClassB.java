package core.di.factory.circular;

import core.annotation.Inject;
import core.annotation.Service;

@Service
public class CircularClassB {
    private CircularClassA circularClassA;

    @Inject
    public CircularClassB(CircularClassA circularClassA) {
        this.circularClassA = circularClassA;
    }
}
