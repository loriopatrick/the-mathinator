
public class Main {
    public static void main (String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");
        Node n = Parser.CreateNode("1+2=x-3", "x");
        System.out.println(Parser.ReadNode(n));
        for (int i = 0; i < 10; i++) {
            if (Solve.Run(n, "x")) break;
            Parser.MarkUp(n);
            System.out.println(Parser.ReadNode(n));
        }
        Parser.MarkUp(n);
        System.out.println(Parser.ReadNode(n));
        System.out.println("Done...");
    }
}