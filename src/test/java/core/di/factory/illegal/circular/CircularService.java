package core.di.factory.illegal.circular;

import core.annotation.Inject;
import core.annotation.Service;

@Service
public class CircularService {

    private CircularRepository circularRepository;

    @Inject
    public CircularService(CircularRepository circularRepository) {
        this.circularRepository = circularRepository;
    }
}
