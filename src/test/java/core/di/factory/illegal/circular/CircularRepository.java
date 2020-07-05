package core.di.factory.illegal.circular;

import core.annotation.Inject;
import core.annotation.Repository;

@Repository
public class CircularRepository {

    private CircularController circularController;

    @Inject
    public CircularRepository(CircularController circularController) {
        this.circularController = circularController;
    }
}
