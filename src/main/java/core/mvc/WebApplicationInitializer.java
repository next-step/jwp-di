package core.mvc;

import javax.servlet.ServletContext;

public interface WebApplicationInitializer {

    void onStartup(ServletContext servletContext);

}
