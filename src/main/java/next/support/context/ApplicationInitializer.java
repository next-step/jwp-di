package next.support.context;

import javax.servlet.ServletContext;

public interface ApplicationInitializer {
    public void onStartup(ServletContext ctx);
}
