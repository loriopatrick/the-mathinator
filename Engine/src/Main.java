
public class Main {
    public static void main (String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");
        Node n = Parser.CreateNode("1+2=2+3+x", "x");
        Solve.Step(n, "x");
        System.out.println("Done...");
    }
}