package next.controller;

import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.mvc.ModelAndView;
import core.mvc.tobe.AbstractNewController;
import next.CannotDeleteException;
import next.dto.QuestionCreateDto;
import next.dto.QuestionUpdateDto;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import next.service.QnaService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class QnaController extends AbstractNewController {

    private QnaService qnaService;

    @Inject
    public QnaController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @RequestMapping(value = "/qna/form", method = RequestMethod.GET)
    public ModelAndView createForm(HttpServletRequest req, HttpServletResponse resp) {
        if (!UserSessionUtils.isLogined(req.getSession())) {
            return jspView("redirect:/users/loginForm");
        }
        return jspView("/qna/form.jsp");
    }

    @RequestMapping(value = "/qna/create", method = RequestMethod.POST)
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            return jspView("redirect:/users/loginForm");
        }
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        QuestionCreateDto questionCreateDto = new QuestionCreateDto(
                request.getParameter("title"),
                request.getParameter("contents"));

        qnaService.insertQuestion(user, questionCreateDto);
        return jspView("redirect:/");
    }

    @RequestMapping(value = "/qna/show", method = RequestMethod.GET)
    public ModelAndView show(HttpServletRequest request, HttpServletResponse response) {
        long questionId = Long.parseLong(request.getParameter("questionId"));

        Question question = qnaService.findById(questionId);
        List<Answer> answers = qnaService.findAllByQuestionId(questionId);

        ModelAndView mav = jspView("/qna/show.jsp");
        mav.addObject("question", question);
        mav.addObject("answers", answers);
        return mav;
    }

    @RequestMapping(value = "/qna/updateForm", method = RequestMethod.GET)
    public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) {
        if (!UserSessionUtils.isLogined(req.getSession())) {
            return jspView("redirect:/users/loginForm");
        }

        long questionId = Long.parseLong(req.getParameter("questionId"));
        Question question = qnaService.updateQuestion(questionId,
                UserSessionUtils.getUserFromSession(req.getSession()));

        return jspView("/qna/update.jsp").addObject("question", question);
    }

    @RequestMapping(value = "/qna/update", method = RequestMethod.POST)
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            return jspView("redirect:/users/loginForm");
        }

        long questionId = Long.parseLong(request.getParameter("questionId"));
        QuestionUpdateDto questionUpdateDto = new QuestionUpdateDto(
                request.getParameter("title"),
                request.getParameter("contents"));

        qnaService.updateQuestion(questionId, questionUpdateDto,
                UserSessionUtils.getUserFromSession(request.getSession()));
        return jspView("redirect:/");
    }

    @RequestMapping(value = "/qna/delete", method = RequestMethod.POST)
    public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) {
        if (!UserSessionUtils.isLogined(req.getSession())) {
            return jspView("redirect:/users/loginForm");
        }
        long questionId = Long.parseLong(req.getParameter("questionId"));
        try {
            qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(req.getSession()));
            return jspView("redirect:/");
        } catch (CannotDeleteException e) {
            return jspView("show.jsp")
                    .addObject("question", qnaService.findById(questionId))
                    .addObject("answers", qnaService.findAllByQuestionId(questionId))
                    .addObject("errorMessage", e.getMessage());
        }
    }
}