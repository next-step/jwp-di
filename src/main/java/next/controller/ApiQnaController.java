package next.controller;

import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.jdbc.DataAccessException;
import core.mvc.ModelAndView;
import core.mvc.tobe.AbstractNewController;
import next.model.Answer;
import next.model.Result;
import next.model.User;
import next.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ApiQnaController extends AbstractNewController {
    private static final Logger logger = LoggerFactory.getLogger(ApiQnaController.class);

    private static final String DEFAULT_URL = "/api/qna";

    private QnaService qnaService;

    @Inject
    public ApiQnaController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @RequestMapping(value = DEFAULT_URL + "/list", method = RequestMethod.GET)
    public ModelAndView questions(HttpServletRequest req, HttpServletResponse resp) {
        return jsonView().addObject("questions", qnaService.findAllQuestion());
    }

    @RequestMapping(value = DEFAULT_URL + "/addAnswer", method = RequestMethod.POST)
    public ModelAndView addAnswer(HttpServletRequest req, HttpServletResponse response) throws Exception {
        if (!UserSessionUtils.isLogined(req.getSession())) {
            return jsonView().addObject("result", Result.fail("Login is required"));
        }

        User user = UserSessionUtils.getUserFromSession(req.getSession());
        Answer answer = new Answer(user.getUserId(),
                req.getParameter("contents"),
                Long.parseLong(req.getParameter("questionId")));
        logger.debug("answer : {}", answer);

        Answer insertAnswer = qnaService.insertAnswer(answer);
        return jsonView().addObject("result", Result.ok())
                .addObject("answer", insertAnswer);

    }

    @RequestMapping(value = DEFAULT_URL + "/deleteAnswer", method = RequestMethod.DELETE)
    public ModelAndView deleteAnswer(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long answerId = Long.parseLong(req.getParameter("answerId"));
        if (!UserSessionUtils.isLogined(req.getSession())) {
            return jsonView().addObject("result", Result.fail("Login is required"));
        }
        User user = UserSessionUtils.getUserFromSession(req.getSession());

        ModelAndView mav = jsonView();
        try {
            qnaService.deleteAnswer(user, answerId);
            mav.addObject("result", Result.ok());
        } catch (DataAccessException e) {
            mav.addObject("result", Result.fail(e.getMessage()));
        }
        return mav;
    }
}
