package next.controller;

import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.jdbc.DataAccessException;
import core.mvc.ModelAndView;
import core.mvc.tobe.AbstractNewController;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.dto.QuestionCreateDto;
import next.dto.QuestionUpdateDto;
import next.model.Answer;
import next.model.Question;
import next.model.Result;
import next.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ApiQnaController extends AbstractNewController {
    private static final Logger logger = LoggerFactory.getLogger(ApiQnaController.class);

    private static final String DEFAULT_URL = "/api/qna";

    private QuestionDao questionDao;
    private AnswerDao answerDao;

    @Inject
    public ApiQnaController(QuestionDao questionDao, AnswerDao answerDao) {
        this.questionDao = questionDao;
        this.answerDao = answerDao;
    }

    @RequestMapping(value = DEFAULT_URL + "/list", method = RequestMethod.GET)
    public ModelAndView questions(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        return jsonView().addObject("questions", questionDao.findAll());
    }

    @RequestMapping(value = DEFAULT_URL, method = RequestMethod.GET)
    public ModelAndView question(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long questionId = Long.parseLong(req.getParameter("questionId"));
        return jsonView().addObject("question", questionDao.findById(questionId));
    }

    @RequestMapping(value = DEFAULT_URL, method = RequestMethod.POST)
    public ModelAndView insertQuestion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        QuestionCreateDto questionCreateDto = objectMapper.readValue(req.getInputStream(), QuestionCreateDto.class);

        Question question = questionDao.insert(
                new Question(questionCreateDto.getWriter(),
                        questionCreateDto.getTitle(),
                        questionCreateDto.getContents()));

        resp.setHeader(HttpHeaders.LOCATION, DEFAULT_URL + "?questionId=" + question.getQuestionId());
        resp.setStatus(HttpStatus.CREATED.value());
        return jsonView();
    }

    @RequestMapping(value = DEFAULT_URL, method = RequestMethod.PUT)
    public ModelAndView updateQuestion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long questionId = Long.parseLong(req.getParameter("questionId"));
        if (null == questionDao.findById(questionId)) {
            return jsonView().addObject("result", Result.fail("Question data doesn't exist."));
        }

        QuestionUpdateDto questionUpdateDto = objectMapper.readValue(req.getInputStream(), QuestionUpdateDto.class);
        questionUpdateDto.update(questionId);
        Question question = objectMapper.convertValue(questionUpdateDto, Question.class);
        questionDao.update(question);
        return jsonView().addObject("result", Result.ok());
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

        Answer savedAnswer = answerDao.insert(answer);
        questionDao.updateCountOfAnswer(savedAnswer.getQuestionId());

        return jsonView().addObject("result", Result.ok())
                .addObject("answer", savedAnswer);

    }

    @RequestMapping(value = DEFAULT_URL + "/deleteAnswer", method = RequestMethod.DELETE)
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
