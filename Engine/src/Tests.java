import com.mathenator.engine.*;
import org.junit.*;

public class Tests {

    public boolean Parse(String eq, Node res) {
        try {
            Node n = Parser.Parse(Parser.Block(eq));
            return n.equals(res);
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void Parse() {
        if (!Parse("(1+2)/3", new Node("/", new Node[]{
                new Node("+", new Node[]{
                        new Node("1"),
                        new Node("2")
                }),
                new Node("3")
        }))) assert false;

        if (!Parse("(1^2)^3", new Node("^", new Node[]{
                new Node("^", new Node[]{
                        new Node("1"),
                        new Node("2")
                }),
                new Node("3")
        }))) assert false;

        if (!Parse("5/((2+x)^(4/5^6))", new Node("/", new Node[]{
                new Node("5"),
                new Node("^", new Node[]{
                        new Node("+", new Node[]{
                                new Node("2"),
                                new Node("x")
                        }),
                        new Node("/", new Node[]{
                                new Node("4"),
                                new Node("^", new Node[]{
                                        new Node("5"),
                                        new Node("6")
                                })
                        })
                })
        }))) assert false;

        if (!Parse("(8+5)*35/5+6", new Node("+", new Node[]{
                new Node("*", new Node[]{
                        new Node("+", new Node[]{
                                new Node("8"),
                                new Node("5")
                        }),
                        new Node("/", new Node[]{
                                new Node("35"),
                                new Node("5")
                        })
                }),
                new Node("6")
        }))) assert false;

        if (!Parse("(x+21)*y+(x*4+2)*y", new Node("+", new Node[]{
                new Node("*", new Node[]{
                        new Node("+", new Node[]{
                                new Node("x"),
                                new Node("21")
                        }),
                        new Node("y")
                }),
                new Node("*", new Node[]{
                        new Node("+", new Node[]{
                                new Node("*", new Node[] {
                                        new Node("x"),
                                        new Node("4")
                                }),
                                new Node("2")
                        }),
                        new Node("y")
                })
        }))) assert false;

        if (!Parse("sin(pi/2)", new Node("sin", new Node[] {
                new Node("/", new Node[] {
                        new Node("pi"),
                        new Node("2")
                })
        }))) assert false;
    }

    public boolean NodeToString(Node n) {
        try {
            Parser.MarkUp(n);
            String val = Parser.ReadNode(n);
            return n.equals(Parser.Parse(Parser.Block(val)));
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void NodeToString() {
        if (!NodeToString(new Node("/", new Node[]{
                new Node("+", new Node[]{
                        new Node("1"),
                        new Node("2")
                }),
                new Node("3")
        }))) assert false;

        if (!NodeToString(new Node("/", new Node[]{
                new Node("5"),
                new Node("^", new Node[]{
                        new Node("+", new Node[]{
                                new Node("2"),
                                new Node("x")
                        }),
                        new Node("/", new Node[]{
                                new Node("4"),
                                new Node("^", new Node[]{
                                        new Node("5"),
                                        new Node("6")
                                })
                        })
                })
        }))) assert false;
    }

    public boolean Simplify(String eq, Node ans) {
        try {
            Node n = Parser.CreateNode(eq);
            for (int i = 0; i < 20; i++) {
                Simplify.Simplify(n);
                Parser.MarkUp(n);
            }
            return n.equals(ans);
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void Simplify() {
        if (!Simplify("10+15-7+2+8-2", new Node("26"))) assert false;

        if (!Simplify("(5*2+6)/(5^2)+10-2/4*6/4", new Node("/", new Node[]{
                new Node("989"),
                new Node("100")
        }))) assert false;

        if (!Simplify("(x^5*y^7*z^-2)/(x^3*z^-10*y)", new Node("*", new Node[]{
                new Node("^", new Node[]{
                        new Node("x"),
                        new Node("2")
                }),
                new Node("^", new Node[]{
                        new Node("y"),
                        new Node("6")
                }),
                new Node("^", new Node[]{
                        new Node("z"),
                        new Node("8")
                })
        }))) assert false;

        if (!Simplify("(x^2)^2", new Node("^", new Node[] {
                new Node("x"),
                new Node("4")
        }))) assert false;

        if (!Simplify("(x^2*5)/x", new Node("*", new Node[] {
                new Node("x"),
                new Node("5")
        }))) assert false;

        if (!Simplify("(x^2)/x", new Node("x"))) assert false;

        if (!Simplify("(x^4)/(x^2)", new Node("^", new Node[] {
                new Node("x"),
                new Node("2")
        }))) assert false;

        if (!Simplify("x/(x^2)", new Node("^", new Node[] {
                new Node("x"),
                new Node("-1")
        }))) assert false;
    }

    public boolean Solve(String eq, Node ans, String target) {
        try {
            Node n = Parser.CreateNode(eq, target);
            for (int i = 0; i < 1000; i++) {
                if (Solve2.Step(n, "x")) break;
                Parser.MarkUp(n);
            }
            Parser.MarkUp(n);

            return n.equals(new Node("=", new Node[]{
                    new Node(target),
                    ans
            }));
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void SolveSimple() {
        if (!Solve("-10*(x-10)^(1/2)=-60", new Node("46"), "x")) assert false;
        if (!Solve("(2*x-88)^(1/2)=(x/6)^(1/2)", new Node("48"), "x")) assert false;
        if (!Solve("(2*x+40)^(1/2)=(-16-2*x)^(1/2)", new Node("-14"), "x")) assert false;
        if (!Solve("(x/10)^(1/2)=(3*x-58)^(1/2)", new Node("20"), "x")) assert false;
        if (!Solve("(x+8)^(1/2)=(3*x+8)^(1/2)", new Node("0"), "x")) assert false;
        if (!Solve("x=x*2", new Node("0"), "x")) assert false;
        if (!Solve("-5*(1-5*x)+5*(-8*x-2)=-4*x-8*x", new Node("-5"), "x")) assert false;
        if (!Solve("2*(4*x-3)-8=4+2*x", new Node("3"), "x")) assert false;
        if (!Solve("8*x-2=-9+7*x", new Node("-7"), "x")) assert false;
        if (!Solve("4/(x-8)=8/2", new Node("9"), "x")) assert false;
        if (!Solve("x/(x-3)=2/3", new Node("-6"), "x")) assert false;
        if (!Solve("(x+10)/(x-7)=8/9", new Node("-146"), "x")) assert false;
        if (!Solve("9/6=x/10", new Node("15"), "x")) assert false;
        if (!Solve("6=(x-2)^(1/2)", new Node("38"), "x")) assert false;
        if (!Solve("-10*(x-10)^(1/2)=-60", new Node("46"), "x")) assert false;
        if (!Solve("4*(9*x+18*x/5)=32*x+6", new Node("/", new Node[] {
                new Node("15"),
                new Node("46")
        }), "x")) assert false;
        if (!Solve("x^2+32=5*x^2", new Node("^", new Node[] {
                new Node("8"),
                new Node("/", new Node[] {
                        new Node("1"),
                        new Node("2")
                })
        }), "x")) assert false;
    }

    @Test
    public void SolveHard () {
        if (!Solve("x=(2-x)^(1/2)", new Node("1"), "x")) assert false;
    }
}
