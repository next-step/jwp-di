package next.controller;

import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.jdbc.DataAccessException;
import core.mvc.ModelAndView;
import core.mvc.tobe.AbstractNewController;
import next.model.Answer;
import next.model.Question;
import next.model.Result;
import next.model.User;
import next.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class ApiQnaController extends AbstractNewController {

    private static final Logger logger = LoggerFactory.getLogger(ApiQnaController.class);

    private final QnaService qnaService;

    @Inject
    public ApiQnaController(final QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @RequestMapping(value = "/api/qna/list", method = RequestMethod.GET)
    public ModelAndView questions(final HttpServletRequest request,
                                  final HttpServletResponse response) throws Exception {
        final List<Question> questions = qnaService.findAllQuestion();

        return jsonView().addObject("questions", questions);
    }

    @RequestMapping(value = "/api/qna/addAnswer", method = RequestMethod.POST)
    public ModelAndView addAnswer(final HttpServletRequest request,
                                  final HttpServletResponse response) throws Exception {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            return jsonView().addObject("result", Result.fail("Login is required"));
        }

        final User user = UserSessionUtils.getUserFromSession(request.getSession());
        final Answer answer = new Answer(user.getUserId(),
                request.getParameter("contents"),
                Long.parseLong(request.getParameter("questionId")));
        logger.debug("answer : {}", answer);

        final Answer savedAnswer = qnaService.save(answer);

        return jsonView().addObject("answer", savedAnswer)
                .addObject("result", Result.ok());
    }

    @RequestMapping(value = "/api/qna/deleteAnswer", method = RequestMethod.POST)
    public ModelAndView deleteAnswer(final HttpServletRequest request,
                                     final HttpServletResponse response) throws Exception {
        final Long answerId = Long.parseLong(request.getParameter("answerId"));

        final ModelAndView mav = jsonView();
        try {
            qnaService.deleteAnswerBy(answerId);
            mav.addObject("result", Result.ok());
        } catch (final DataAccessException e) {
            mav.addObject("result", Result.fail(e.getMessage()));
        }

        return mav;
    }
}
