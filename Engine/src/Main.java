import com.mathenator.engine.Node;
import com.mathenator.engine.Parser;
import com.mathenator.engine.Simplify;
import com.mathenator.engine.Solve;

public class Main {
    public static void main (String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");
        Node n = Parser.CreateNode("x*2=x", "x"); // needs fixing in parser!!
        System.out.println(Parser.ReadNode(n));
        for (int i = 0; i < 5000; i++) {
            if (Solve.Step(n, "x")) break;
            Parser.MarkUp(n);
            System.out.println(Parser.ReadNode(n));
        }
        Parser.MarkUp(n);
        System.out.println(Parser.ReadNode(n));
        System.out.println("Done...");
    }
}