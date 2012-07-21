import com.mathenator.engine.*;

public class Experiment {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");

//        Simplify.Run("(6*x*y)/(3)");

//        Solve2.Run("2*x*y+4*x*y=2*(2*x+y)", "x");
        Solve2.Run("4*x*y*z+6*x*(6*y+8*z)=19*x^2", "x");

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