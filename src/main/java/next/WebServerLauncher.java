package next;

public class WebServerLauncher {

    public static void main(String[] args) throws Exception {
        try (final Server server = new Server()) {
            server.start();
            server.await();
        }
    }
}
