package core.web;

import core.mvc.DispatcherServlet;
import core.web.context.WebApplicationContext;
import core.web.filter.CharacterEncodingFilter;
import next.WebServerLauncher;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebApplicationInitializer implements ApplicationInitializer {

    private static final String DEFAULT_DISPATCHER_SERVLET_NAME = "dispatcherServlet";
    private static final String DEFAULT_ENCODING_FILTER_NAME = "encodingFilter";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        Package rootPackage = WebServerLauncher.class.getPackage();

        ServletRegistration.Dynamic dispatcherServlet = servletContext.addServlet(DEFAULT_DISPATCHER_SERVLET_NAME, new DispatcherServlet(new WebApplicationContext(rootPackage.getName())));
        dispatcherServlet.setLoadOnStartup(1);
        dispatcherServlet.addMapping("/");

        FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(DEFAULT_ENCODING_FILTER_NAME, new CharacterEncodingFilter());
        filterRegistration.addMappingForUrlPatterns(null, false, "/*");
    }
}
