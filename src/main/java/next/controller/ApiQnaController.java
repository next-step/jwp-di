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
import next.dto.QuestionDto;
import next.model.Answer;
import next.model.Question;
import next.model.Result;
import next.model.User;
import next.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ApiQnaController extends AbstractNewController {
    private static final Logger logger = LoggerFactory.getLogger( ApiQnaController.class );

    private ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    private QnaService qnaService;

    @RequestMapping(value = "/api/qna/list", method = RequestMethod.GET)
    public ModelAndView questions(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        return jsonView().addObject("questions", qnaService.findAll());
    }

    @RequestMapping(value = "/api/qna", method = RequestMethod.POST)
    public ModelAndView addQuestion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        QuestionDto questionDto = objectMapper.readValue(req.getInputStream(), QuestionDto.class);
        final Question savedQuestion = qnaService.insert(questionDto.toQuestion());

        resp.setHeader("Location", "/api/qna?questionId=" + savedQuestion.getQuestionId());
        resp.setStatus(HttpStatus.CREATED.value());
        return new ModelAndView(new JsonView());
    }

    @RequestMapping(value = "/api/qna", method = RequestMethod.GET)
    public ModelAndView question(HttpServletRequest req, HttpServletResponse resp) {
        final long questionId = Long.parseLong(req.getParameter("questionId"));
        return jsonView().addObject("question", qnaService.findById(questionId));
    }

    @RequestMapping(value = "/api/qna", method = RequestMethod.PUT)
    public ModelAndView putQuestion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long questionId = Long.valueOf(req.getParameter("questionId"));
        QuestionDto questionDto = objectMapper.readValue(req.getInputStream(), QuestionDto.class);
        qnaService.update(questionId, questionDto.toQuestion());
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

        Answer savedAnswer = qnaService.insert(answer);
        qnaService.updateCountOfAnswer(savedAnswer.getQuestionId());

        return jsonView().addObject("answer", savedAnswer).addObject("result", Result.ok());
    }

    @RequestMapping(value = "/api/qna/deleteAnswer", method = RequestMethod.POST)
    public ModelAndView deleteAnswer(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long answerId = Long.parseLong(req.getParameter("answerId"));

        ModelAndView mav = jsonView();
        try {
            qnaService.delete(answerId);
            mav.addObject("result", Result.ok());
        } catch (DataAccessException e) {
            mav.addObject("result", Result.fail(e.getMessage()));
        }
        return mav;
    }
}
