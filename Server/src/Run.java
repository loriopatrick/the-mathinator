import com.mathenator.server.Server;

public class Run {
    public static void main (String[] args) throws Exception {
        Server server = new Server(80);
        server.start("www");
    }
}
