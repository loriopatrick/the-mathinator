import com.mathinator.engine.*;

public class Experiment {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");

        Solve.Run("(5+x)/12=9/3", "x");
//        Solve.Run("d(2x+5x)", "x");
//        Node test = Parser.CreateNode("2^(3/2),-2^(3/2)");
//        System.out.println(test);

//        System.out.println(nMath.Multiples("-10"));
//        Simplify.Run("");
//        System.out.println(Parser.ReadNodeLatex(Parser.CreateNode("d((x^2)/6*x+x^5,x)")));
//
//        Solve.Run("2*x*y+4*x*y=2*(2*x+y)", "x");
//        Solve.Run("3+2+5/x*=*x/(4+x)", "x");
    }
}