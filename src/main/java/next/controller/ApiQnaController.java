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
import next.repository.JdbcAnswerRepository;
import next.repository.JdbcQuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ApiQnaController extends AbstractNewController {
    private static final Logger logger = LoggerFactory.getLogger( ApiQnaController.class );

    private JdbcQuestionRepository questionRepository;
    private JdbcAnswerRepository answerRepository;

    @Inject
    public ApiQnaController(JdbcQuestionRepository questionRepository, JdbcAnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @RequestMapping(value = "/api/qna/list", method = RequestMethod.GET)
    public ModelAndView questions(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        return jsonView().addObject("questions", questionRepository.findAll());
    }

    @RequestMapping(value = "/api/qna/addAnswer", method = RequestMethod.POST)
    public ModelAndView addAnswer(HttpServletRequest req, HttpServletResponse response) throws Exception {
        if (!UserSessionUtils.isLogined(req.getSession())) {
            return jsonView().addObject("result", Result.fail("Login is required"));
        }

        User user = UserSessionUtils.getUserFromSession(req.getSession());
        Answer answer = new Answer(user.getUserId(), req.getParameter("contents"),
                Long.parseLong(req.getParameter("questionId")));
        logger.debug("answer : {}", answer);

        Answer savedAnswer = answerRepository.insert(answer);
        questionRepository.updateCountOfAnswer(savedAnswer.getQuestionId());

        return jsonView().addObject("answer", savedAnswer).addObject("result", Result.ok());
    }

    @RequestMapping(value = "/api/qna/deleteAnswer", method = RequestMethod.POST)
    public ModelAndView deleteAnswer(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long answerId = Long.parseLong(req.getParameter("answerId"));

        ModelAndView mav = jsonView();
        try {
            answerRepository.delete(answerId);
            mav.addObject("result", Result.ok());
        } catch (DataAccessException e) {
            mav.addObject("result", Result.fail(e.getMessage()));
        }
        return mav;
    }
}
