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

            eq.changed = true;
            return;
        }

        // need to check if needs factoring

        // NEEDS WORK OPEN
        if (y.value.equals("0")) {
            if (x.value.equals("+")) {
                Node[] parts = new Node[5];
                boolean[] filled = new boolean[5];

                boolean bad = false;
                for (int i = 0; i < x.nodes.size(); i++) {
                    Node c = x.nodes.get(i);
                    if (c.targets > 0) {

                        Node a, b;

                        if (c.value.equals("*") && c.nodes.size() == 2) {
                            a = c.nodes.get(0);
                            b = c.nodes.get(1);

                            if (a.targets == 0) {
                                Node temp = a;
                                a = b;
                                b = temp;
                            }
                        } else {
                            a = c;
                            b = new Node("1");
                        }

                        if (a.targets == 0) {
                            bad = true;
                            break;
                        }

                        if (a.value.equals(target)) {
                            if (filled[0]) {
                                bad = true;
                                break;
                            }

                            filled[1] = true;
                            parts[1] = b;
                        } else if (a.value.equals("^")) {
                            if (!a.nodes.get(0).value.equals(target) || !Bools.isNum(a.nodes.get(1).value)) {
                                bad = true;
                                break;
                            }

                            float ex = Float.parseFloat(a.nodes.get(1).value);
                            if (ex != Math.floor(ex)) {
                                bad = true;
                                break;
                            }

                            int exI = (int)ex;
                            if (filled[exI]) {
                                bad = true;
                                break;
                            }

                            filled[exI] = true;
                            parts[exI] = b;
                        }
                    } else if (c.targets == 0) {
                        if (filled[0]) {
                            bad = true;
                            break;
                        }

                        filled[0] = true;
                        parts[0] = c;
                    }
                }

                if (!bad) {
                    eq.nodes.set(toLeft? 0 : 1, new Node(target));
                    eq.nodes.set(toLeft? 1 : 0, new Node("/", new Node[] {
                            new Node("+/-", new Node[] {
                                    new Node("*", new Node[] {
                                            new Node("-1"),
                                            parts[1].clone()
                                    }),
                                    new Node("^", new Node[] {
                                            new Node("+", new Node[] {
                                                    new Node("^", new Node[] {
                                                            parts[1].clone(),
                                                            new Node("2")
                                                    }),
                                                    new Node("*", new Node[] {
                                                            new Node("-4"),
                                                            parts[2].clone(),
                                                            parts[1].clone()
                                                    })
                                            }),
                                            new Node("/", new Node[] {
                                                    new Node("1"),
                                                    new Node("2")
                                            })
                                    })
                            }),
                            new Node("*", new Node[] {
                                    new Node("2"),
                                    parts[2].clone()
                            })
                    }));

                    return;
                }
            }
        }
        // CLOSE

        if (x.value.equals("+") && y.value.equals("+")) {
            for (int i = 0; i < x.nodes.size(); i++) {
                Node c = x.nodes.get(i);

                if (c.targets == 0) {
                    Node temp = new Node("*", new Node[]{
                            new Node("-1"),
                            c
                    });
                    temp.changed = true;
                    y.nodes.add(temp);
                    x.nodes.remove(i);

                    return;
                }
            }

            for (int i = 0; i < y.nodes.size(); i++) {
                Node c = y.nodes.get(i);

                if (c.targets > 0) {
                    Node temp = new Node("*", new Node[]{
                            new Node("-1"),
                            c
                    });
                    temp.changed = true;
                    x.nodes.add(temp);
                    y.nodes.remove(i);

                    return;
                }
            }
        } else if (x.value.equals("+")) {
            for (int i = 0; i < x.nodes.size(); i++) {
                Node c = x.nodes.get(i);

                if (c.targets == 0) {
                    int pos = toLeft ? 1 : 0;
                    Node temp = new Node("*", new Node[]{
                            new Node("-1"),
                            c
                    });
                    temp.changed = true;
                    eq.nodes.set(pos, new Node("+", new Node[]{
                            eq.nodes.get(pos),
                            temp
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
                    Node temp = new Node("*", new Node[]{
                            new Node("-1"),
                            c
                    });
                    temp.changed = true;
                    eq.nodes.set(pos, new Node("+", new Node[]{
                            eq.nodes.get(pos),
                            temp
                    }));
                    y.nodes.remove(i);

                    return;
                }
            }
        }

        if (y.targets > 0 && x.targets > 0 && x.height <= 2 && y.height <= 2) {
            if (y.value.equals("*") || x.value.equals("*")) {
                eq.nodes.set(toLeft ? 0 : 1, new Node("+", new Node[]{
                        x,
                        new Node("*", new Node[]{
                                new Node("-1"),
                                y

                        })
                }));

                eq.nodes.set(toLeft ? 1 : 0, new Node("0"));

                eq.nodes.get(1).changed = true;
                eq.nodes.get(0).changed = true;
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

                            y.nodes.get(1).changed = true;
                            return;
                        } else {
                            y.nodes.set(1, new Node("*", new Node[]{
                                    y.nodes.get(1),
                                    c
                            }));

                            x.nodes.remove(i);

                            y.nodes.get(1).changed = true;
                            return;
                        }
                    } else {
                        int pos = toLeft ? 1 : 0;

                        eq.nodes.set(pos, new Node("/", new Node[]{
                                y,
                                c
                        }));

                        x.nodes.remove(i);

                        eq.nodes.get(pos).changed = true;
                        return;
                    }
                }
            }
        } else if (x.value.equals("/")) {
            if (x.nodes.get(1).targets == 0) {
                eq.nodes.set(toLeft ? 0 : 1, x.nodes.get(0));
                eq.nodes.set(toLeft ? 1 : 0, new Node("*", new Node[]{
                        y,
                        x.nodes.get(1)
                }));
            }
        } else if (x.value.equals("^")) {
            if (x.nodes.get(1).targets == 0) {
                eq.nodes.set(toLeft ? 1 : 0, new Node("^", new Node[]{
                        y,
                        new Node("/", new Node[]{
                                new Node("1"),
                                x.nodes.get(1)
                        })
                }));
                x.value = x.nodes.get(0).value;
                x.nodes = x.nodes.get(0).nodes;

                x.changed = true;
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


    public static void Run(String eq, String target) throws Exception {
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
