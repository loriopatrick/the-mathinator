import java.util.ArrayList;

public class Tests {
    public static boolean Parse() {
        Node n;
        try {
            n = Parser.Parse(Parser.Block("(1+2)/3"));
            if (!n.equals(new Node("/", new Node[]{
                    new Node("+", new Node[]{
                            new Node("1"),
                            new Node("2")
                    }),
                    new Node("3")
            }))) return false;

            n = Parser.Parse(Parser.Block("(1^2)^3"));
            if (!n.equals(new Node("^", new Node[]{
                    new Node("^", new Node[]{
                            new Node("1"),
                            new Node("2")
                    }),
                    new Node("3")
            }))) return false;

            n = Parser.Parse(Parser.Block("5/((2+x)^(4/5^6))"));
            if (!n.equals(new Node("/", new Node[]{
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
            }))) return false;

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean NodeToString() {
        Node n;
        String val;
        try {
            n = new Node("/", new Node[]{
                    new Node("+", new Node[]{
                            new Node("1"),
                            new Node("2")
                    }),
                    new Node("3")
            });
            Parser.MarkUp(n);
            val = Parser.ReadNode(n);
            if (!n.equals(Parser.Parse(Parser.Block(val)))) return false;


            n = new Node("/", new Node[]{
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
            });
            Parser.MarkUp(n);
            val = Parser.ReadNode(n);
            if (!n.equals(Parser.Parse(Parser.Block(val)))) return false;


        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean Simplify() {
        Node n;
        try {
            n = Parser.CreateNode("10+15-7+2+8-2");
            for (int i = 0; i < 20; i++) {
                Simplify.Simplify(n);
                Parser.MarkUp(n);
            }
            if (!n.equals(new Node("26"))) return false;


            n = Parser.CreateNode("(5*2+6)/(5^2)+10-2/4*6/4");
            for (int i = 0; i < 20; i++) {
                Simplify.Simplify(n);
                Parser.MarkUp(n);
            }
            if (!n.equals(new Node("/", new Node[] {
                    new Node("989"),
                    new Node("100")
            }))) return false;


            n = Parser.CreateNode("(x^5*y^7*z^-2)/(x^3*z^-10*y)");
            for (int i = 0; i < 20; i++) {
                Simplify.Simplify(n);
                Parser.MarkUp(n);
            }

            if (!n.equals(new Node("*", new Node[] {
                    new Node("^", new Node[] {
                            new Node("x"),
                            new Node("2")
                    }),
                    new Node("^", new Node[] {
                            new Node("y"),
                            new Node("6")
                    }),
                    new Node("^", new Node[] {
                            new Node("z"),
                            new Node("8")
                    })
            }))) return false;


        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String Run() {
        StringBuilder sb = new StringBuilder();
        sb.append("* Parse: ").append(Parse() ? "success" : "failure").append('\n');
        sb.append("* NodeToString: ").append(NodeToString() ? "success" : "failure").append('\n');
        sb.append("* Simplify: ").append(Simplify()? "success" : "failure").append('\n');

        return sb.toString();
    }
}
