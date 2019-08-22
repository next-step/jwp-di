package next.controller;

import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.mvc.ModelAndView;
import core.mvc.tobe.AbstractNewController;
import next.repository.JdbcQuestionRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HomeController extends AbstractNewController {

    private final JdbcQuestionRepository jdbcQuestionRepository;

    @Inject
    public HomeController(JdbcQuestionRepository jdbcQuestionRepository) {
        this.jdbcQuestionRepository = new JdbcQuestionRepository();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return jspView("home.jsp").addObject("questions", jdbcQuestionRepository.findAll());
    }
}
