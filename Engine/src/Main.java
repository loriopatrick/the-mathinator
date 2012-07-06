
public class Main {
    public static void main (String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");
        Node n = Parser.CreateNode("1+2=3+4+x", "x");
        for (int i = 0; i < 10; i++) {
            Solve.Run(n, "x");
            System.out.println(Parser.ReadNode(n));
        }
        System.out.println("Done...");
    }
}