package core.mvc.tobe;

import core.annotation.web.Controller;
import core.annotation.web.RequestMethod;
import core.di.factory.ApplicationContext;
import core.mvc.HandlerMapping;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationHandlerMapping implements HandlerMapping {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private final HandlerExecutions handlerExecutions = new HandlerExecutions();
    private final ApplicationContext ac;

    public AnnotationHandlerMapping(ApplicationContext applicationContext) {
        this.ac = applicationContext;
    }

    public void initialize() {
        logger.info("## Initialized Annotation Handler Mapping");
        Map<Class<?>, Object> controllers = ac.getBeansAnnotatedWith(Controller.class);
        controllers.forEach((key, value) -> handlerExecutions.addHandlerExecution(value, key.getMethods()));
    }

    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        logger.debug("requestUri : {}, requestMethod : {}", requestUri, rm);
        return handlerExecutions.getHandler(new HandlerKey(requestUri, rm));
    }
}
