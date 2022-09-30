package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.RequestMethod;
import core.di.factory.container.ApplicationContext;
import core.mvc.HandlerMapping;
import core.mvc.exception.NotFoundException;
import core.mvc.tobe.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static java.util.Arrays.asList;

public class AnnotationHandlerMapping implements HandlerMapping {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private final ApplicationContext applicationContext;
    private final Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    public AnnotationHandlerMapping(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void initialize() {
        logger.info("## Initialized Annotation Handler Mapping");
        applicationContext.initialize();
        handlerExecutions.putAll(new RequestHandlerConverter(asList(
                new HttpRequestArgumentResolver(),
                new HttpResponseArgumentResolver(),
                new RequestParamArgumentResolver(),
                new PathVariableArgumentResolver(),
                new ModelArgumentResolver()
        )).handlers(applicationContext.getControllers()));
    }

    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        logger.debug("requestUri : {}, requestMethod : {}", requestUri, rm);
        return getHandlerInternal(new HandlerKey(requestUri, rm));
    }

    private HandlerExecution getHandlerInternal(HandlerKey requestHandlerKey) {
        for (HandlerKey handlerKey : handlerExecutions.keySet()) {
            if (handlerKey.isMatch(requestHandlerKey)) {
                return handlerExecutions.get(handlerKey);
            }
        }
        throw new NotFoundException("Request Uri와 일치하는 정보가 존재하지 않습니다.");
    }
}
