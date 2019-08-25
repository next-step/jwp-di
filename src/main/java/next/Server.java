package next;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Server implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final Tomcat tomcat = new Tomcat();

    public Server() {
        this("webapp/", 8080);
    }

    public Server(final String webappDirLocation,
                  final int port) {
        tomcat.setPort(port);

        tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        logger.info("configuring app with basedir: {}", new File("./" + webappDirLocation).getAbsolutePath());
    }

    public void start() throws LifecycleException {
        tomcat.start();
    }

    @Override
    public void close() throws LifecycleException {
        tomcat.stop();
    }

    public void await() {
        tomcat.getServer().await();
    }
}
