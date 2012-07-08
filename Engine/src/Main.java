import com.mathenator.engine.Node;
import com.mathenator.engine.Parser;
import com.mathenator.engine.Simplify;
import com.mathenator.engine.Solve;

public class Main {
    public static void main (String[] args) throws Exception {
        System.out.println("Welcome... to the Mathenator!");

        Simplify.Run("1+(1+x)/(3+x)");
        /*
            1/(2*x)+(x-1)/(2*x^2)
            ((x-1)*(2*x)+(2*x^2))/((2*x^2)*(x-1))
            ( 2*x^2 - 2*x + 2*x^2 )
            ( 4*x^2 - 2*x )
         */

    }
}