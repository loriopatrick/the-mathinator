package com.mathenator.engine;

public class Solve {
    public static void Solve(Node eq, String target) {
        if (!eq.value.equals("=")) return;

        Parser.MarkUp(eq, target);
        if (eq.targets == 0) return;

        Node x = eq.nodes.get(0),
                y = eq.nodes.get(1);

        boolean toLeft = eq.nodes.get(0).targets >= eq.nodes.get(1).targets;

        if (!toLeft) {
            Node temp = y;
            y = x;
            x = temp;
        }

        if (x.equals(y)) {
            eq.nodes.set(1, new Node("0"));
            eq.nodes.set(0, new Node("0"));
            return;
        }

        if (x.value.equals("+") && y.value.equals("+")) {
            for (int i = 0; i < x.nodes.size(); i++) {
                Node c = x.nodes.get(i);

                if (c.targets == 0) {
                    y.nodes.add(new Node("*", new Node[]{
                            new Node("-1"),
                            c
                    }));
                    x.nodes.remove(i);
                    return;
                }
            }

            for (int i = 0; i < y.nodes.size(); i++) {
                Node c = y.nodes.get(i);

                if (c.targets > 0) {
                    x.nodes.add(new Node("*", new Node[]{
                            new Node("-1"),
                            c
                    }));
                    y.nodes.remove(i);
                    return;
                }
            }
        } else if (x.value.equals("+")) {
            for (int i = 0; i < x.nodes.size(); i++) {
                Node c = x.nodes.get(i);

                if (c.targets == 0) {
                    int pos = toLeft ? 1 : 0;
                    eq.nodes.set(pos, new Node("+", new Node[]{
                            eq.nodes.get(pos),
                            new Node("*", new Node[]{
                                    new Node("-1"),
                                    c
                            })
                    }));

                    x.nodes.remove(i);
                    return;
                }
            }
        } else if (y.value.equals("+")) {
            for (int i = 0; i < y.nodes.size(); i++) {
                Node c = y.nodes.get(i);

                if (c.targets > 0) {
                    int pos = toLeft ? 0 : 1;
                    eq.nodes.set(pos, new Node("+", new Node[]{
                            eq.nodes.get(pos),
                            new Node("*", new Node[]{
                                    new Node("-1"),
                                    c
                            })
                    }));
                    y.nodes.remove(i);
                    return;
                }
            }
        }

        if (y.targets > 0 && x.targets > 0 && x.height <= 2 && y.height <= 2) {
            if (y.value.equals("*") || x.value.equals("*")) {
                eq.nodes.set(toLeft? 0 : 1, new Node("+", new Node[] {
                        x,
                        new Node("*", new Node[] {
                                new Node("-1"),
                                y

                        })
                }));

                eq.nodes.set(toLeft? 1 : 0, new Node("0"));
                return;
            }
        }

        if (x.value.equals("*")) {
            for (int i = 0; i < x.nodes.size(); i++) {
                Node c = x.nodes.get(i);

                if (c.targets == 0) {
                    if (y.value.equals("/")) {
                        if (y.nodes.get(1).value.equals("*")) {
                            y.nodes.get(1).nodes.add(c);

                            x.nodes.remove(i);
                            return;
                        } else {
                            y.nodes.set(1, new Node("*", new Node[]{
                                    y.nodes.get(1),
                                    c
                            }));

                            x.nodes.remove(i);
                            return;
                        }
                    } else {
                        int pos = toLeft ? 1 : 0;

                        eq.nodes.set(pos, new Node("/", new Node[] {
                                y,
                                c
                        }));

                        x.nodes.remove(i);
                        return;
                    }
                }
            }
        } else if (x.value.equals("/")) {
            if (x.nodes.get(1).targets == 0) {
                eq.nodes.set(toLeft? 0 : 1, x.nodes.get(0));
                eq.nodes.set(toLeft? 1 : 0, new Node("*", new Node[] {
                        y,
                        x.nodes.get(1)
                }));
            }
        } else if (x.value.equals("^")) {
            if (x.nodes.get(1).targets == 0) {
                eq.nodes.set(toLeft? 1 : 0, new Node("^", new Node[] {
                        y,
                        new Node("/", new Node[] {
                                new Node("1"),
                                x.nodes.get(1)
                        })
                }));
                x.value = x.nodes.get(0).value;
                x.nodes = x.nodes.get(0).nodes;
                return;
            }
        }

        if (y.value.equals("^")) {

        }
    }

    public static boolean Step(Node eq, String target, boolean e) {
        Node last = eq.clone();

        Parser.MarkUp(eq, target);
        if (!Simplify.Step(eq)) return false;
        Solve.Solve(eq, target);

        if (!e && eq.equals(last)) {
            return Step(eq, target, true);
        }
        return eq.equals(last);
    }

    public static boolean Step(Node eq, String target) {
        return Step(eq, target, false);
    }


    public static void Run (String eq, String target) throws Exception {
        System.out.println("\nRUN...");
        Node n = Parser.CreateNode(eq, target);
        System.out.println(Parser.ReadNode(n));
        int i;
        for (i = 0; i < 5000; i++) {
            if (Solve.Step(n, target)) break;
            Parser.MarkUp(n);
            System.out.println(Parser.ReadNode(n));
        }
        Parser.MarkUp(n);
        System.out.println(Parser.ReadNode(n));
        System.out.println("Done... " + i);
    }
}
