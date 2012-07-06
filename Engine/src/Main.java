import com.mathenator.engine.Node;
import com.mathenator.engine.Parser;
import com.mathenator.engine.Simplify;
import com.mathenator.engine.Solve;

public class Main {
    public static void main (String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");
        Node n = Parser.CreateNode("2*x-3+56*x=3*(x+6)", "x"); // 2*x-3+56*x=x+6
        // 58.0*x+-3.0*(x+6)
        System.out.println(Parser.ReadNode(n));
        for (int i = 0; i < 1000; i++) {
            if (Solve.Step(n, "x")) break;
            Parser.MarkUp(n);
            System.out.println(Parser.ReadNode(n));
        }
        Parser.MarkUp(n);
        System.out.println(Parser.ReadNode(n));
        System.out.println("Done...");
    }
}