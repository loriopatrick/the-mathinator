package com.mathenator.engine;

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
    }

    public boolean Solve(String eq, Node ans, String target) {
        try {
            Node n = Parser.CreateNode(eq, target);
            for (int i = 0; i < 1000; i++) {
                if (Solve.Step(n, "x")) break;
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
    public void Solve() {
        if (!Solve("-10*((x-10)^(1/2))=-60", new Node("46"), "x")) assert false;
        if (!Solve("(2*x-88)^(1/2)=(x/6)^(1/2)", new Node("48"), "x")) assert false;
    }
}
