
public class Main {
    public static void main (String[] args) throws Exception {
        Node test = Parser.CreateNode("(1+2)/3"); //1+2*(3+5*(6+x)+x)*x^2=9
        System.out.println(Parser.ReadNode(test));
        for (int i = 0; i < 30; i++) {
            boolean res = SimplifyRec.Simplify(test);
            Parser.MarkUp(test);
            if (res)
                System.out.println(Parser.ReadNode(test));
        }
        System.out.println("DONE: " + Parser.ReadNode(test));
    }
}