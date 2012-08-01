import com.mathinator.engine.Simplify;
import com.mathinator.engine.*;

public class Experiment {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");

//        Solve2.Run("(x-5)/7=(x+4)/3", "x");
        Simplify.Run("(x^5*y^7*z^-2)/(x^3*z^-10*y)");
//        System.out.println(Parser.ReadNodeLatex(Parser.CreateNode("d((x^2)/6*x+x^5,x)")));
//
//        Solve2.Run("2*x*y+4*x*y=2*(2*x+y)", "x");
//        Solve2.Run("3+2+5/x*=*x/(4+x)", "x");
    }
}