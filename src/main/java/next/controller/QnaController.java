package next.controller;

import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.mvc.ModelAndView;
import core.mvc.tobe.AbstractNewController;
import next.CannotDeleteException;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import next.service.QnaService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class QnaController extends AbstractNewController {

    private final QnaService qnaService;

    @Inject
    public QnaController(final QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @RequestMapping(value = "/qna/form", method = RequestMethod.GET)
    public ModelAndView createForm(final HttpServletRequest request,
                                   final HttpServletResponse response) throws Exception {
        if (isNotLogin(request)) {
            return jspView("redirect:/users/loginForm");
        }

        return jspView("/qna/form.jsp");
    }

    private boolean isNotLogin(final HttpServletRequest request) {
        return !UserSessionUtils.isLogined(request.getSession());
    }

    @RequestMapping(value = "/qna/create", method = RequestMethod.POST)
    public ModelAndView create(final HttpServletRequest request,
                               final HttpServletResponse response) throws Exception {
        if (isNotLogin(request)) {
            return jspView("redirect:/users/loginForm");
        }

        final User user = UserSessionUtils.getUserFromSession(request.getSession());
        final Question question = new Question(user.getUserId(),
                request.getParameter("title"),
                request.getParameter("contents"));

        qnaService.save(question);
        return jspView("redirect:/");
    }

    @RequestMapping(value = "/qna/show", method = RequestMethod.GET)
    public ModelAndView show(final HttpServletRequest request,
                             final HttpServletResponse response) throws Exception {
        final long questionId = Long.parseLong(request.getParameter("questionId"));

        final Question question = qnaService.findQuestionBy(questionId);
        final List<Answer> answers = qnaService.findAllAnswerBy(questionId);

        final ModelAndView mav = jspView("/qna/show.jsp");
        mav.addObject("question", question);
        mav.addObject("answers", answers);

        return mav;
    }

    @RequestMapping(value = "/qna/updateForm", method = RequestMethod.GET)
    public ModelAndView updateForm(final HttpServletRequest request,
                                   final HttpServletResponse response) throws Exception {
        if (isNotLogin(request)) {
            return jspView("redirect:/users/loginForm");
        }

        final long questionId = Long.parseLong(request.getParameter("questionId"));
        final User user = UserSessionUtils.getUserFromSession(request.getSession());
        final Question question = qnaService.findQuestionBy(questionId, user);

        return jspView("/qna/update.jsp").addObject("question", question);
    }

    @RequestMapping(value = "/qna/update", method = RequestMethod.POST)
    public ModelAndView update(final HttpServletRequest request,
                               final HttpServletResponse response) throws Exception {
        if (isNotLogin(request)) {
            return jspView("redirect:/users/loginForm");
        }

        final long questionId = Long.parseLong(request.getParameter("questionId"));
        final User user = UserSessionUtils.getUserFromSession(request.getSession());

        final Question newQuestion = new Question(user.getUserId(),
                request.getParameter("title"),
                request.getParameter("contents"));

        qnaService.update(questionId, newQuestion, user);

        return jspView("redirect:/");
    }

    @RequestMapping(value = "/qna/delete", method = RequestMethod.GET)
    public ModelAndView delete(final HttpServletRequest request,
                               final HttpServletResponse response) throws Exception {
        if (isNotLogin(request)) {
            return jspView("redirect:/users/loginForm");
        }

        final long questionId = Long.parseLong(request.getParameter("questionId"));
        final User user = UserSessionUtils.getUserFromSession(request.getSession());

        try {
            qnaService.deleteQuestionBy(questionId, user);
            return jspView("redirect:/");
        } catch (final CannotDeleteException e) {
            return jspView("show.jsp")
                    .addObject("question", qnaService.findQuestionBy(questionId))
                    .addObject("answers", qnaService.findAllAnswerBy(questionId))
                    .addObject("errorMessage", e.getMessage());
        }
    }
}
