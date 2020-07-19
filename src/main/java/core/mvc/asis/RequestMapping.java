package core.mvc.asis;

import core.mvc.HandlerMapping;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestMapping implements HandlerMapping {

    private static final Logger logger = LoggerFactory.getLogger(RequestMapping.class);
    private Map<String, Controller> mappings = new HashMap<>();

    public void initialize() {
        logger.info("Initialized Request Mapping!");
        mappings.keySet().forEach(path -> logger.info("Path : {}, Controller : {}", path, mappings.get(path).getClass()));
    }

    public Controller getHandler(HttpServletRequest request) {
        return mappings.get(request.getRequestURI());
    }

    void put(String url, Controller controller) {
        mappings.put(url, controller);
    }
}
