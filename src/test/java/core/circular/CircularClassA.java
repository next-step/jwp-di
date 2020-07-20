package core.circular;

import core.annotation.Inject;
import core.annotation.Service;

@Service
public class CircularClassA {
    private CircularClassB circularClassB;

    @Inject
    public CircularClassA(CircularClassB circularClassB) {
        this.circularClassB = circularClassB;
    }

}
