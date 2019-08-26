package next.controller;

import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.mvc.ModelAndView;
import core.mvc.tobe.AbstractNewController;
import next.repository.QuestionRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HomeController extends AbstractNewController {

    private QuestionRepository questionRepository;

    @Inject
    public HomeController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return jspView("home.jsp").addObject("questions", questionRepository.findAll());
    }
}