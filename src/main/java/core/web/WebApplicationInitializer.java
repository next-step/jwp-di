package core.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Created by iltaek on 2020/07/19 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public interface WebApplicationInitializer {

    void onStartup(ServletContext servletContext) throws ServletException;
}
