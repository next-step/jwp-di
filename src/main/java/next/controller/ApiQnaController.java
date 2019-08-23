package next.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.jdbc.DataAccessException;
import core.mvc.JsonView;
import core.mvc.ModelAndView;
import core.mvc.tobe.AbstractNewController;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.dto.QnaCreatedDto;
import next.dto.QnaUpdatedDto;
import next.model.Answer;
import next.model.Question;
import next.model.Result;
import next.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Controller
public class ApiQnaController extends AbstractNewController {
    private static final Logger logger = LoggerFactory.getLogger( ApiQnaController.class );

    ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    private QuestionDao questionDao;

    @Inject
    private AnswerDao answerDao;

    private long questionId = 0;

    @RequestMapping(value = "/api/qna/list", method = RequestMethod.GET)
    public ModelAndView questions(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        return jsonView().addObject("questions", questionDao.findAll());
    }

    @RequestMapping(value = "/api/qna", method = RequestMethod.POST)
    public ModelAndView addQuestion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        QnaCreatedDto question = objectMapper.readValue(req.getInputStream(), QnaCreatedDto.class);
        final Question savedQuestion = questionDao.insert(question.toQuestion());

        resp.setHeader("Location", "/api/qna?questionId=" + savedQuestion.getQuestionId());
        resp.setStatus(HttpStatus.CREATED.value());
        return new ModelAndView(new JsonView());
    }

    @RequestMapping(value = "/api/qna", method = RequestMethod.GET)
    public ModelAndView question(HttpServletRequest req, HttpServletResponse resp) {
        final long questionId = Long.parseLong(req.getParameter("questionId"));
        return jsonView().addObject("question", questionDao.findById(questionId));
    }

    @RequestMapping(value = "/api/qna", method = RequestMethod.PUT)
    public ModelAndView putQuestion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long questionId = Long.valueOf(req.getParameter("questionId"));
        QnaUpdatedDto updatedDto = objectMapper.readValue(req.getInputStream(), QnaUpdatedDto.class);
        final Question question = questionDao.findById(questionId);
        question.update(updatedDto.toQuestion());
        questionDao.update(question);
        return new ModelAndView(new JsonView());
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

        Answer savedAnswer = answerDao.insert(answer);
        questionDao.updateCountOfAnswer(savedAnswer.getQuestionId());

        return jsonView().addObject("answer", savedAnswer).addObject("result", Result.ok());
    }

    @RequestMapping(value = "/api/qna/deleteAnswer", method = RequestMethod.POST)
    public ModelAndView deleteAnswer(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long answerId = Long.parseLong(req.getParameter("answerId"));

        ModelAndView mav = jsonView();
        try {
            answerDao.delete(answerId);
            mav.addObject("result", Result.ok());
        } catch (DataAccessException e) {
            mav.addObject("result", Result.fail(e.getMessage()));
        }
        return mav;
    }
}
