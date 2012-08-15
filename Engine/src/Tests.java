import com.mathinator.engine.*;

import org.junit.*;

import java.util.ArrayList;

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
                                new Node("*", new Node[]{
                                        new Node("x"),
                                        new Node("4")
                                }),
                                new Node("2")
                        }),
                        new Node("y")
                })
        }))) assert false;

        if (!Parse("sin(pi/2)", new Node("sin", new Node[]{
                new Node("/", new Node[]{
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
                Simplify.Simplify(n, true);
                Parser.MarkUp(n);
            }
            return n.equals(ans);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean Simplify(String eq, String ans) {
        try {
            return Simplify(eq, Parser.CreateNode(ans));
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void Simplify() {
        if (!Simplify("10+15-7+2+8-2", "26")) assert false;
        if (!Simplify("(5*2+6)/(5^2)+10-2/4*6/4", "989/100")) assert false;
        if (!Simplify("(x^5*y^7*z^-2)/(x^3*z^-10*y)", "x^2*y^6*z^8")) assert false;
        if (!Simplify("(x^2)^2", "x^4")) assert false;
        if (!Simplify("(x^2*5)/x", "5*x")) assert false;
        if (!Simplify("(x^2)/x", "x")) assert false;
        if (!Simplify("(x^4)/(x^2)", "x^2")) assert false;
        if (!Simplify("x/(x^2)", "x^(-1)")) assert false;
        if (!Simplify("5*x*y+3*x*y+9*x*z*y+12*x*z*y", "8*x*y+21*x*z*y")) assert false;
        if (!Simplify("(6*y*x+7*x)/x", "6*y+7")) assert false;
        if (!Simplify("(2*(x-3))+(4*b)+(-2*(x-b-3))+5", "6*b+5")) assert false;
        if (!Simplify("-5/-13", "5/13")) assert false;
    }

    public boolean Solve(String eq, Node ans, String target) {
        try {
            Node n = Parser.CreateNode(eq, target);
            for (int i = 0; i < 1000; i++) {
                if (Solve2.Step(n, "x")) break;
                Parser.MarkUp(n);
            }
            Parser.MarkUp(n);

            if (ans.valEquals(",")) {
                for (Node c : ans.nodes) {
                    c.clone(new Node("=", new Node[] {
                            new Node(target),
                            c.clone()
                    }));
                }

                return n.equals(ans);
            }

            return n.equals(new Node("=", new Node[]{
                    new Node(target),
                    ans
            }));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean Solve(String eq, String res, String target) {
        try {
            return Solve(eq, Parser.CreateNode(res, target), target);
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void SolveSimple() {
        if (!Solve("-10*(x-10)^(1/2)=-60", "46", "x")) assert false;
        if (!Solve("(2*x-88)^(1/2)=(x/6)^(1/2)", "48", "x")) assert false;
        if (!Solve("(2*x+40)^(1/2)=(-16-2*x)^(1/2)", "-14", "x")) assert false;
        if (!Solve("(x/10)^(1/2)=(3*x-58)^(1/2)", "20", "x")) assert false;
        if (!Solve("(x+8)^(1/2)=(3*x+8)^(1/2)", "0", "x")) assert false;
        if (!Solve("x=x*2", "0", "x")) assert false;
        if (!Solve("-5*(1-5*x)+5*(-8*x-2)=-4*x-8*x", "-5", "x")) assert false;
        if (!Solve("2*(4*x-3)-8=4+2*x", "3", "x")) assert false;
        if (!Solve("8*x-2=-9+7*x", "-7", "x")) assert false;
        if (!Solve("4/(x-8)=8/2", "9", "x")) assert false;
        if (!Solve("x/(x-3)=2/3", "-6", "x")) assert false;
        if (!Solve("(x+10)/(x-7)=8/9", "-146", "x")) assert false;
        if (!Solve("9/6=x/10", "15", "x")) assert false;
        if (!Solve("6=(x-2)^(1/2)", "38", "x")) assert false;
        if (!Solve("-10*(x-10)^(1/2)=-60", "46", "x")) assert false;
        if (!Solve("4*(9*x+18*x/5)=32*x+6", "15/46", "x")) assert false;
        if (!Solve("x^2+32=5*x^2", "2^(3/2),-1*2^(3/2)", "x")) assert false;
        if (!Solve("3*(6-1*(8*x-7))+6*x=0", "13/6", "x")) assert false;

        //1+2/x=9*x+9
    }

    @Test
    public void SolveHard() {
        if (!Solve("2*x*y+4*x=5", "5/(2*y+4)", "x")) assert false;
        if (!Solve("2*x*y+4*x*y=2*(2*x+y)", "y/(3*y-2)", "x")) assert false;
        if (!Solve("x^2+2*x+1=0", "-1,-1", "x")) assert false;
    }

    @Test
    public void nMath() {
        try {
            ArrayList<Integer> a = nMath.Multiples("-4");
            if (a.size() < 2) assert false;
            if (!a.contains(-2)) assert false;
            if (!a.contains(2)) assert false;
        } catch (Exception e) {
            assert false;
        }
    }
}
