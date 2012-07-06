
public class Main {
    public static void main (String[] args) throws Exception {
//        System.out.println("Welcome... to the Mathenator!");
//        Node n = Parser.CreateNode("1+2=2+3+x", "x");
//        Solve.Step(n, "x");
//        System.out.println("Done...");

        Node n = Parser.CreateNode("(8+5)*35/5+6");
        Parser.ReadNode(n);
        for (int i = 0; i < 30; i++) {
            boolean simp = Simplify.Simplify(n);
            Parser.MarkUp(n);
            if (simp) System.out.println(Parser.ReadNode(n));
        }
        Parser.ReadNode(n);

    }
}