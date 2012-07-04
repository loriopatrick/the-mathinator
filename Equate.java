import java.util.ArrayList;

public class Equate {

    public static Node Add(Node a, Node b) {
        if (isNum(a.value) && isNum(b.value)) {
            float res = Float.parseFloat(a.value) + Float.parseFloat(b.value);
            return new Node(res + "");
        }

        if (a.equals(b)) {
            if (a.value.equals("*")) {
                a.nodes.add(0, new Node("2"));
                return a;
            }
            return new Node("*", new Node[]{
                    new Node("2"),
                    a
            });
        }

        if (a.value.equals("/") && b.value.equals("/")) {
            if (a.nodes.get(1).equals(b.nodes.get(1))) {
                return new Node("/", new Node[]{
                        new Node("+", new Node[]{
                                a.nodes.get(0),
                                b.nodes.get(0)
                        }),
                        a.nodes.get(1)
                });
            }

            return new Node("/", new Node[]{
                    new Node("+", new Node[]{
                            new Node("*", new Node[]{
                                    b.nodes.get(1),
                                    a.nodes.get(0)
                            }),
                            new Node("*", new Node[]{
                                    a.nodes.get(1),
                                    b.nodes.get(0)
                            })
                    }),
                    new Node("*", new Node[]{
                            a.nodes.get(1),
                            b.nodes.get(1)
                    })
            });
        }

        if (a.value.equals("/") || b.value.equals("/")) {
            Node div, base;
            if (a.value.equals("/")) {
                div = a;
                base = b;
            } else {
                div = b;
                base = a;
            }
            return new Node("/", new Node[]{
                    new Node("+", new Node[]{
                            new Node("*", new Node[]{
                                    base,
                                    div.nodes.get(1)
                            }),
                            div.nodes.get(0)
                    }),
                    div.nodes.get(1)
            });
        }

        return null;
    }

    public static Node Multiply (Node a, Node b) {
        if (isNum(a.value) && isNum(b.value)) {
            float res = Float.parseFloat(a.value) * Float.parseFloat(b.value);
            return new Node(res + "");
        }

        if (a.equals(b)) {
            return new Node("^", new Node[] {
                    a,
                    new Node("2")
            });
        }

        if (a.value.equals("*") || b.value.equals("*")) {
            if (a.value.equals("*")) {
                a.nodes.add(b);
                return a;
            }

            b.nodes.add(a);
            return b;
        }

        return null;
    }

    public static Node Divide (Node n, Node d) {

        if (n.equals(d)) return new Node("1");

        if (isNum(n.value) && isNum(d.value)) {
            float a = Float.parseFloat(n.value),
                    b = Float.parseFloat(d.value);

            float res = a / b;

            if (Math.floor(res) == res)
                return new Node(res + "");

            int[] Simple = nMath.SimplifyFraction(a, b);
            if (Simple != null && Simple[0] != a) {
                return new Node("/", new Node[] {
                        new Node(Simple[0] + ""),
                        new Node(Simple[1] + "")
                });
            }

            return null;
        }

//        if (n.value.equals("*") && d.value.equals("*")) {
//            Node res = new Node("/", new Node[] {
//                    new Node("*"),
//                    new Node("*")
//            });
//            for (Node i : n.nodes) {
//                int ci;
//                if (i.temp2 == -1) i.temp2 =
//                int better = 0;
//                Node best;
//                for (Node l : d.nodes) {
//                    if (better == 0) better = ci + ? 1 : 2;
//                    Node temp = Divide(i, l);
//                    if (temp == null) continue;
//                    int complex = Children(temp);
//                    if (complex == 1) {
//                        res.nodes.get(0).nodes.add(temp);
//                    }
//                }
//            }
//        }

        return null;
    }

    public static Node Simplify (Node n) {
        if (n.simple) return n;
        if (isNumOrChar(n.value)) return n;
        if (n.nodes.size() == 0) return n;

//        for (Node c : n.nodes) {
//            Simplify(c);
//        }

        Node temp = null;
        if (n.value.equals("*")) {
            temp = Multiply(n.nodes.get(0), n.nodes.get(1));
        } else if (n.value.equals("+")) {
            temp = Add(n.nodes.get(0), n.nodes.get(1));
        }

        if (temp == null) {
            n.nodes.set(0, Simplify(n.nodes.get(0)));
            n.nodes.set(1, Simplify(n.nodes.get(1)));
            return Simplify(n);
        }

        if (n.nodes.size() > 2) {
            n.nodes.remove(0);
            n.nodes.set(0, temp);
        } else {
            return temp;
        }

        return null;
    }


    public static int Children (Node n) {
        int res = 1;
        for (Node c : n.nodes) {
            res += Children(c);
        }
        return res;
    }

    public static boolean isNumOrChar(String s) {
        return s.length() > 0 && isNumOrChar(s.charAt(0));
    }

    public static boolean isNumOrChar(char c) {
        return (isNum(c) || (c >= 97 && c <= 122) || (c >= 65 && c <= 90));
    }

    public static boolean isNum(String c) {
        return isNum(c.charAt(0));
    }

    public static boolean isNum(char c) {
        return ((c >= 48 && c <= 57) || c == '-' || c == '.');
    }

    public static boolean SimilarFrames(Node a, Node b) {
        if (!a.value.equals(b.value)) {
            if (!(isNumOrChar(a.value) && isNumOrChar(b.value))) return false;
        }

        for (Node na : a.nodes) {
            boolean in = false;
            for (Node nb : b.nodes) {
                if (SimilarFrames(na, nb)) {
                    in = true;
                    break;
                }
            }

            if (!in) return false;
        }

        return true;
    }

    public static Node addNode() {
        return new Node("+", new Node[]{
                new Node("*", new Node[]{
                        new Node("x")
                }),
                new Node("*", new Node[]{
                        new Node("x")
                })
        });
    }

    public static boolean Similar(Node search, Node subject) {
        if (!search.value.equals(subject.value)) return false;
        for (int i = 0; i < search.nodes.size(); i++) {
            boolean in = false;
            for (int j = 0; j < subject.nodes.size(); j++) {
                if (Similar(search.nodes.get(i), subject.nodes.get(j))) {
                    in = true;
                    break;
                }
            }
            if (!in) return false;
        }
        return true;
    }

    public static Node[] Find(Node search, Node subject) {
        ArrayList<Node> finds = new ArrayList<Node>();
        if (Similar(search, subject)) finds.add(subject);
        for (int i = 0; i < subject.nodes.size(); i++) {
            Node[] found = Find(search, subject.nodes.get(i));
            for (Node aFound : found) finds.add(aFound);
        }
        Node[] res = new Node[finds.size()];
        finds.toArray(res);
        return res;
    }
}
