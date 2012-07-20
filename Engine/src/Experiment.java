import com.mathenator.engine.*;

public class Experiment {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");

        Simplify.Run("5*x*y+3*x*y+9*x*z*y+12*x*z*y");

//        Solve2.Run("x=(2-x)^(1/2)", "x");

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