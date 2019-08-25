package core.mvc.tobe;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.mvc.JsonView;
import core.mvc.JspView;
import core.mvc.ModelAndView;

public abstract class AbstractNewController {

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected ModelAndView jspView(String forwardUrl) {
        return new ModelAndView(new JspView(forwardUrl));
    }

    protected ModelAndView jsonView() {
        return new ModelAndView(new JsonView());
    }
}
