package next.security;

import core.mvc.tobe.MethodParameter;
import core.mvc.tobe.support.AbstractAnnotationArgumentResolver;
import next.controller.UserSessionUtils;
import next.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginUserArgumentResolver extends AbstractAnnotationArgumentResolver {
    @Override
    public boolean supports(MethodParameter methodParameter) {
        return supportAnnotation(methodParameter, LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        if (UserSessionUtils.isLogined(request.getSession())) {
            return UserSessionUtils.getUserFromSession(request.getSession());
        }

        if (requiredLogin(methodParameter)) {
            sendLoginPage(response);
        }

        return User.GUEST_USER;
    }

    private void sendLoginPage(HttpServletResponse response) {
        try {
            response.sendRedirect("/users/loginForm");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean requiredLogin(MethodParameter methodParameter) {
        LoginUser loginUser = getAnnotation(methodParameter, LoginUser.class);
        return loginUser.required();
    }
}
