package next;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;


public interface AppInitializer {

    public void onStartup(ServletContext ctx) throws ServletException;
}
