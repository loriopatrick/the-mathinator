import com.mathenator.server.Server;

public class Run {
    public static void main (String[] args) throws Exception {
        System.out.println(args.length);
        Server server = new Server(Integer.parseInt(args[1]));
        server.start(args[2]);
    }
}
