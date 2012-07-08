package com.mathenator.engine;

import sun.tools.tree.NewArrayExpression;

public class Simplify {
    public static boolean Simplify(Node node) {
        node.changed = false;
        node.message = null;

        for (int i = 0; i < node.nodes.size(); i++) {
            if (!node.nodes.get(i).simple)
                if (Simplify(node.nodes.get(i))) return true;
        }
        if (node.nodes.size() == 0) {
            if (node.value.equals("+")) {
                node.value = "0";

                node.changed = true;
                return true;
            } else if (node.value.equals("*")) {
                node.value = "1";

                node.changed = true;
                return true;
            }
            return false;
        }
        if (!Bools.isFn(node.value) && node.nodes.size() == 1) {
            Node n = node.nodes.get(0);
            node.nodes.clear();
            node.value = n.value;
            node.nodes.addAll(n.nodes);
        }
        if (node.parent != null && node.value.equals(node.parent.value)) {
            if (!(node.value.equals("^") || node.value.equals("/"))) {
                node.parent.nodes.remove(node);
                node.parent.nodes.addAll(node.nodes);
                node.parent.simple = false;
                node = node.parent;
            }
        }
        if (node.value.equals("+")) {
            float sum = 0;
            for (int i = 0; i < node.nodes.size(); ++i) {
                Node a = node.nodes.get(i);
                for (int j = 0; j < node.nodes.size(); ++j) {
                    if (i == j) continue;

                    Node b = node.nodes.get(j);

                    if (Bools.isNum(a.value) && Bools.isNum(b.value)) {
                        sum = Float.parseFloat(a.value) + Float.parseFloat(b.value);

                        Node temp = new Node(sum + "");
                        temp.changed = true;
                        temp.message = "Added " + a.value
                                + " and " + b.value
                                + " and got " + sum + '\n'
                                + '(' + a.value + '+' + b.value + '=' + sum + ')';

                        if (i > j) {
                            node.nodes.remove(i);
                            node.nodes.set(j, temp);
                        } else {
                            node.nodes.remove(j);
                            node.nodes.set(i, temp);
                        }

                        return true;

                    } else if (a.equals(b)) {
                        Node temp = new Node("*", new Node[]{
                                new Node("2"),
                                a
                        });

                        temp.changed = true;
                        temp.message = "x+x = 2*x";

                        if (i > j) {
                            node.nodes.remove(i);
                            node.nodes.set(j, temp);
                        } else {
                            node.nodes.remove(j);
                            node.nodes.set(i, temp);
                        }

                        return true;
                    } else if (a.value.equals("*")) {
                        if (b.value.equals("*")) {
                            if (b.nodes.size() == 2) {
                                for (int n = 0; n <= 1; n++) {
                                    for (int g = 0; g <= 1; g++) {
                                        if (a.nodes.get(n).equals(b.nodes.get(g))) {
                                            int n1 = n == 1 ? 0 : 1,
                                                    g1 = g == 1 ? 0 : 1;

//                                            if (Bools.isNum(a.nodes.get(n1).value) && Bools.isNum(b.nodes.get(g1).value)) {
                                                a.nodes.set(n1, new Node("+", new Node[] {
                                                        a.nodes.get(n1),
                                                        b.nodes.get(g1)
                                                }));
                                                node.nodes.remove(j);

                                                a.changed = true;
                                                a.message = "x*y+z*y = y*(x+z)";
                                                return true;
//                                            }

//                                            if (a.nodes.get(n1).value.equals("/")
//                                                    && a.nodes.get(n1).height == 1
//                                                    && a.nodes.get(n1).targets == 0
//
//                                                    && b.nodes.get(g1).value.equals("/")
//                                                    && b.nodes.get(g1).height == 1
//                                                    && b.nodes.get(g1).targets == 0) {
//
//
//                                                a.nodes.set(n1, new Node("+", new Node[]{
//                                                        a.nodes.get(n1),
//                                                        b.nodes.get(g1)
//                                                }));
//
//                                                node.nodes.remove(j);
//
//                                                node.changed = true;
//                                                return true;
//                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (a.nodes.size() == 2) {
                                if (a.nodes.get(0).equals(b)) {
                                    if (Bools.isNum(a.nodes.get(1).value)) {
                                        a.nodes.get(1).value = (Float.parseFloat(a.nodes.get(1).value) + 1) + "";
                                        node.nodes.remove(j);

                                        node.changed = true;
                                        return true;
                                    }
                                } else if (a.nodes.get(1).equals(b)) {
                                    if (Bools.isNum(a.nodes.get(0).value)) {
                                        a.nodes.get(0).value = (Float.parseFloat(a.nodes.get(0).value) + 1) + "";
                                        node.nodes.remove(j);

                                        node.changed = true;
                                        return true;
                                    }
                                }
                            }
                        }
                    } else if (a.value.equals("/") && b.value.equals("/")) {
                        if ((a.targets > 0 && b.targets > 0) || (a.targets == 0 && b.targets == 0)) {
                            if (a.nodes.get(1).equals(b.nodes.get(1))) {
                                node.nodes.set(i, new Node("/", new Node[]{
                                        new Node("+", new Node[]{
                                                a.nodes.get(0),
                                                b.nodes.get(0)
                                        }),
                                        a.nodes.get(1)
                                }));
                                node.nodes.remove(j);

                                node.changed = true;
                                return true;
                            }

                            node.nodes.set(i, new Node("/", new Node[] {
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
                            }));

                            node.nodes.remove(j);

                            node.changed = true;
                            return true;
                        }

                    } else if (a.value.equals("/") || b.value.equals("/")) {
                        Node div, base;
                        if (a.value.equals("/")) {
                            div = a;
                            base = b;
                        } else {
                            div = b;
                            base = a;
                        }

                        if (div.targets > 0 && base.targets > 0 || div.targets == 0 && base.targets == 0) {
                            node.nodes.set(i, new Node("/", new Node[]{
                                    new Node("+", new Node[]{
                                            new Node("*", new Node[]{
                                                    base,
                                                    div.nodes.get(1)
                                            }),
                                            div.nodes.get(0)
                                    }),
                                    div.nodes.get(1)
                            }));

                            node.nodes.remove(j);

                            node.changed = true;
                            return true;
                        }
                    }
                }
            }
        } else if (node.value.equals("*")) {
            for (int i = 0; i < node.nodes.size(); ++i) {
                Node a = node.nodes.get(i);
                for (int j = 0; j < node.nodes.size(); ++j) {
                    if (i == j) continue;
                    Node b = node.nodes.get(j);

                    if (Bools.isNum(a.value)) {
                        float val = Float.parseFloat(a.value);
                        if (val == 1) {
                            node.nodes.remove(i);

                            node.changed = true;
                            return true;
                        }

                        if (val == 0) {
                            node.value = "0";
                            node.nodes.clear();

                            node.changed = true;
                            return true;
                        }
                    }

                    if (Bools.isNum(a.value) && Bools.isNum(b.value)) {
                        float res = Float.parseFloat(a.value) * Float.parseFloat(b.value);
                        node.nodes.set(i, new Node(res + ""));
                        node.nodes.remove(j);

                        node.changed = true;
                        return true;
                    }

                    if (Bools.isNum(a.value) && b.value.equals("+")) {
                        for (int n = 0; n < b.nodes.size(); n++) {
                            b.nodes.set(n, new Node("*", new Node[] {
                                    b.nodes.get(n),
                                    a
                            }));
                        }

                        node.nodes.remove(i);

                        node.changed = true;
                        return true;
                    }

                    if (a.equals(b)) {
                        node.nodes.set(i, new Node("^", new Node[]{
                                a,
                                new Node("2")
                        }));
                        node.nodes.remove(j);

                        node.changed = true;
                        return true;
                    }

                    if (a.value.equals("^")) {
                        if (b.value.equals("^")) {
                            if (a.nodes.get(0).equals(b.nodes.get(0))) {
                                Node temp = new Node("+", new Node[]{
                                        a.nodes.get(1),
                                        b.nodes.get(1)
                                });

                                a.nodes.set(1, temp);
                                node.nodes.remove(j);

                                node.changed = true;
                                return true;
                            }
                        } else {
                            if (Bools.isNum(a.nodes.get(1).value) && a.nodes.get(0).equals(b)) {
                                a.nodes.get(1).value = (Float.parseFloat(a.nodes.get(1).value) + 1) + "";
                                node.nodes.remove(j);

                                node.changed = true;
                                return true;
                            }
                        }
                    }

                    if (a.value.equals("/")) {
                        if (b.value.equals("/")) {
                            node.nodes.set(i, new Node("/", new Node[]{
                                    new Node("*", new Node[]{
                                            a.nodes.get(0),
                                            b.nodes.get(0)
                                    }),
                                    new Node("*", new Node[]{
                                            a.nodes.get(1),
                                            b.nodes.get(1)
                                    })
                            }));
                            node.nodes.remove(j);

                            node.changed = true;
                            return true;
                        }

                        node.nodes.set(i, new Node("/", new Node[]{
                                new Node("*", new Node[]{
                                        a.nodes.get(0),
                                        b
                                }),
                                a.nodes.get(1)
                        }));

                        node.nodes.remove(j);

                        node.changed = true;
                        return true;
                    }
                }
            }
        } else if (node.value.equals("/")) {

            Node n = node.nodes.get(0),
                    d = node.nodes.get(1);

            if (n.value.equals("0")) {
                node.value = "0";
                node.nodes.clear();

                node.changed = true;
                return true;
            }

            if (n.equals(d)) {
                node.value = "1";
                node.nodes.clear();

                node.changed = true;
                return true;
            }

            if (Bools.isNum(n.value) && Bools.isNum(d.value)) {
                float a = Float.parseFloat(n.value),
                        b = Float.parseFloat(d.value);

                float res = a / b;

                if (Math.floor(res) == res) {
                    node.value = res + "";
                    node.nodes.clear();

                    node.changed = true;
                    return true;
                }

                int[] Simple = nMath.SimplifyFraction(a, b);
                if (Simple != null && Simple[0] != a) {
                    node.nodes.set(0, new Node(Simple[0] + ""));
                    node.nodes.set(1, new Node(Simple[1] + ""));

                    node.changed = true;
                    return true;
                }
            }

            if (n.value.equals("/") && d.value.equals("/")) {
                node.nodes.set(0, new Node("*", new Node[]{
                        n.nodes.get(0),
                        d.nodes.get(1)
                }));
                node.nodes.set(1, new Node("*", new Node[]{
                        n.nodes.get(1),
                        d.nodes.get(0)
                }));

                node.changed = true;
                return true;
            }

            if (n.value.equals("/")) {
                node.nodes.set(0, n.nodes.get(0));
                node.nodes.set(1, new Node("*", new Node[]{
                        n.nodes.get(1),
                        d
                }));

                node.changed = true;
                return true;
            }

            if (d.value.equals("/")) {
                // 1 / (1/2)
                node.nodes.set(0, new Node("*", new Node[]{
                        n,
                        d.nodes.get(1)
                }));
                node.nodes.set(1, d.nodes.get(0));

                node.changed = true;
                return true;
            }

            if (n.value.equals("*") && d.value.equals("*")) {
                for (int i = 0; i < n.nodes.size(); i++) {
                    Node a = n.nodes.get(i);
                    for (int j = 0; j < d.nodes.size(); j++) {
                        Node b = d.nodes.get(j);

                        if (a.equals(b)) {
                            n.nodes.remove(i);
                            d.nodes.remove(j);

                            node.changed = true;
                            return true;
                        }

                        if (a.value.equals("^") && b.value.equals("^")) {
                            if (a.nodes.get(0).equals(b.nodes.get(0))) {
                                a.nodes.set(1, new Node("+", new Node[]{
                                        a.nodes.get(1),
                                        new Node("*", new Node[]{
                                                new Node("-1"),
                                                b.nodes.get(1)
                                        })
                                }));
                                d.nodes.remove(j);

                                node.changed = true;
                                return true;
                            }
                        } else if (a.value.equals("^")) {
                            if (a.nodes.get(0).equals(b)) {
                                a.nodes.set(1, new Node("+", new Node[] {
                                        a.nodes.get(1),
                                        new Node("-1")
                                }));
                                d.nodes.remove(j);

                                node.changed = true;
                                return true;
                            }
                        } else if (b.value.equals("^")) {
                            if (b.nodes.get(0).equals(a)) {
                                b.nodes.set(1, new Node("+", new Node[] {
                                        b.nodes.get(1),
                                        new Node("-1")
                                }));
                                n.nodes.remove(i);

                                node.changed = true;
                                return true;
                            }
                        }
                    }
                }
            } else if (n.value.equals("*")) {
                for (int i = 0; i < n.nodes.size(); i++) {
                    Node a = n.nodes.get(i);
                    if (a.equals(d)) {
                        n.nodes.remove(i);
                        node.nodes.remove(1);

                        node.changed = true;
                        return true;
                    }

                    if (a.value.equals("^") && d.value.equals("^")) {
                        if (a.nodes.get(0).equals(d.nodes.get(0))) {
                            a.nodes.set(1, new Node("+", new Node[]{
                                    a.nodes.get(1),
                                    new Node("*", new Node[]{
                                            new Node("-1"),
                                            d.nodes.get(1)
                                    })
                            }));
                            node.nodes.remove(1);

                            node.changed = true;
                            return true;
                        }
                    }
                }
            } else if (n.value.equals("+")) {
                Node targets = new Node("+");
                Node norms = new Node("+");

                for (int i = 0; i < n.nodes.size(); i++) {
                    Node c = n.nodes.get(i);
                    if (c.targets > 0) {
                        targets.nodes.add(c);
                    } else {
                        norms.nodes.add(c);
                    }
                }

                if (targets.nodes.size() > 0 && norms.nodes.size() > 0) {
                    node.value = "+";
                    node.nodes.clear();
                    node.nodes.add(new Node("/", new Node[] {
                            targets,
                            d
                    }));
                    node.nodes.add(new Node("/", new Node[] {
                            norms,
                            d
                    }));

                    node.changed = true;
                    return true;
                }
            }

        } else if (node.value.equals("^")) {
            Node b = node.nodes.get(0),
                    e = node.nodes.get(1);

            if (e.value.equals("1")) {
                node.value = b.value;
                node.nodes = b.nodes;

                node.changed = true;
                return true;
            }

            if (e.value.equals("0")) {
                node.value = "1";
                node.nodes.clear();

                node.changed = true;
                return true;
            }

            if (Bools.isNum(b.value) && Bools.isNum(e.value)) {
                double val = Math.pow(Float.parseFloat(b.value), Float.parseFloat(e.value));
                node.value = val + "";
                node.nodes.clear();

                node.changed = true;
                return true;
            } else if (Bools.isNum(b.value) && e.equals(new Node("/", new Node[] {
                    new Node("1"),
                    new Node("2")
            }))) {
                double val = Math.sqrt(Float.parseFloat(b.value));
                if (val == Math.floor(val)) {
                    node.value = val + "";
                    node.nodes.clear();

                    node.changed = true;
                    return true;
                }
            } else if (b.value.equals("^")) {
                b.nodes.set(1, new Node("*", new Node[] {
                        b.nodes.get(1),
                        e
                }));
                node.value = b.value;
                node.nodes = b.nodes;

                node.changed = true;
                return true;
            }
        }

        return false;
    }

    public static boolean Step (Node node) {
        Node last = node.clone();

        int count = 0;
        while (!Simplify(node)) {
            if (++count > 5) break;
        }

        return node.equals(last);
    }

    public static void Run (String eq) throws Exception{
        Node n = Parser.CreateNode(eq);
        Parser.MarkUp(n);
        System.out.println(Parser.ReadNode(n));
        for (int i = 0; i < 1000; i++) {
            Parser.MarkUp(n);
            System.out.println(Parser.ReadNode(n));
            if (Step(n)) break;
        }
    }
}
