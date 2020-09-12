package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.RequestMethod;
import core.di.config.ConfigurationBeanScanner;
import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanScanner;
import core.mvc.HandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class AnnotationHandlerMapping implements HandlerMapping {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private Object[] basePackage;
    private ClasspathBeanScanner classpathBeanScanner;
    private ConfigurationBeanScanner configurationBeanScanner;
    private BeanFactory beanFactory;

    private Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
        this.beanFactory = new BeanFactory();
        this.configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        this.classpathBeanScanner = new ClasspathBeanScanner(beanFactory);
    }

    public void initialize() {
        logger.info("## Initialized Annotation Handler Mapping");
        this.configurationBeanScanner.scan("");
        handlerExecutions.putAll(classpathBeanScanner.scan(basePackage));
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

        return null;
    }
}
