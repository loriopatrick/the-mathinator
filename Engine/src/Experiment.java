import com.mathenator.engine.*;

public class Experiment {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");

        Simplify.Run("d((x^2)/6+x^5,x)");

//        System.out.println(Parser.ReadNodeLatex(Parser.CreateNode("d((x^2)/6*x+x^5,x)")));
//
//        Solve2.Run("2*x*y+4*x*y=2*(2*x+y)", "x");
//        Solve2.Run("3+2+5/x*=*x/(4+x)", "x");

        //   (1+2)/3 :: !(true && true)
        //   -10*(x-10)^(1/2) :: !(true && false)

        //   !(eq[i - 1].equals(")") && eq[start].equals(")"))

        /*
            1/(2*x)+(x-1)/(2*x^2)
            ((x-1)*(2*x)+(2*x^2))/((2*x^2)*(x-1))
            ( 2*x^2 - 2*x + 2*x^2 )
            ( 4*x^2 - 2*x )
         */

    }
}