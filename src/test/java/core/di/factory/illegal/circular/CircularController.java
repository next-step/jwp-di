package core.di.factory.illegal.circular;

import core.annotation.Inject;
import core.annotation.web.Controller;

@Controller
public class CircularController {

    private CircularService circularService;

    @Inject
    public CircularController(CircularService circularService) {
        this.circularService = circularService;
    }
}
