import com.mathinator.engine.Simplify;
import com.mathinator.engine.*;

import java.awt.datatransfer.StringSelection;

public class Experiment {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");

//        Solve2.Run("8/x-3*x=4*x+6/x", "x");
        Solve2.Run("4/x^2+5/x+5=0/x", "x");
//        Node test = Parser.CreateNode("2^(3/2),-2^(3/2)");
//        System.out.println(test);

//        Simplify.Run("8^(1/2)");
//        System.out.println(Parser.ReadNodeLatex(Parser.CreateNode("d((x^2)/6*x+x^5,x)")));
//
//        Solve2.Run("2*x*y+4*x*y=2*(2*x+y)", "x");
//        Solve2.Run("3+2+5/x*=*x/(4+x)", "x");
    }
}