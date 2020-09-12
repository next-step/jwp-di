package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.RequestMethod;
import core.di.ApplicationContext;
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
    private BeanFactory beanFactory;

    private Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    public AnnotationHandlerMapping(final ApplicationContext applicationContext) {
        this.classpathBeanScanner = new ClasspathBeanScanner(applicationContext.getBeanFactory());
        this.basePackage = applicationContext.getBasePackage();
        this.beanFactory = applicationContext.getBeanFactory();
    }

    public void initialize() {
        logger.info("## Initialized Annotation Handler Mapping");
        handlerExecutions.putAll(classpathBeanScanner.scan(basePackage));
    }

    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        logger.debug("requestUri : {}, requestMethod : {}", requestUri, rm);
        return getHandlerInternal(new HandlerKey(requestUri, rm));
    }

    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
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
