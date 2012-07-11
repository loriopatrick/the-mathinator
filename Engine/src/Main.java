import com.mathenator.engine.Node;
import com.mathenator.engine.Parser;
import com.mathenator.engine.Simplify;
import com.mathenator.engine.Solve;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");

        Solve.Run("(x/10)^(1/2)=(3*x-58)^(1/2)", "x");

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