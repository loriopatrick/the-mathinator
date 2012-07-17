package com.mathenator.engine;

import com.sun.org.apache.xerces.internal.impl.dv.xs.YearDV;

public class Solve2 {

    public static boolean Solve (Node node, String target) {
        if (!node.value.equals("=")) return false;

        Parser.MarkUp(node, target);
        if (node.targets == 0) return false;

        Node x = node.nodes.get(0),
                y = node.nodes.get(1);

        boolean toLeft = node.nodes.get(0).targets >= node.nodes.get(1).targets;

        if (!toLeft) {
            Node temp = y;
            y = x;
            x = temp;
        }

        if (x.equals(y)) {
            node.nodes.set(1, new Node("0"));
            node.nodes.set(0, new Node("0"));

            node.changed = true;
            return true;
        }

        if (Adds(x, y)) return true;
        if (Fracs(x, y)) return true;


        if ("a".equals("b")) return false;

        return false;
    }

    public static boolean Pows (Node x, Node y) {
        if (x.value.equals("^")
                && y.targets == 0
                && x.nodes.get(1).targets == 0
                && x.nodes.get(0).targets == 1
                && x.nodes.get(0).height == 0) {

            Node temp = new Node("^", new Node[] {

            });
        }

        return false;
    }

    public static boolean Fracs (Node x, Node y) {
        if (x.value.equals("/") && y.value.equals("/")) {

            // a/x = b/x :: a = b
            if (x.nodes.get(1).equals(y.nodes.get(1))) {
                x.value = x.nodes.get(0).value;
                x.nodes = x.nodes.get(0).nodes;

                y.value = y.nodes.get(0).value;
                y.nodes = y.nodes.get(0).nodes;

                return true;
            }

            if (y.targets > 0) {
                // cross multiply
                Node temp = new Node("*", new Node[] {
                        x.nodes.get(0),
                        y.nodes.get(1)
                });

                Node temp2 = new Node("*", new Node[] {
                        y.nodes.get(0),
                        x.nodes.get(1)
                });

                x.value = temp.value;
                x.nodes = temp.nodes;
                y.value = temp2.value;
                y.nodes = temp2.nodes;

                return true;
            }
        }
        return false;
    }

    public static boolean Adds (Node x, Node y) {

        if (x.value.equals("+")) {
            // go through x and find any non targets to push away
            for (int i = 0; i < x.nodes.size(); i++) {
                Node c = x.nodes.get(i);

                if (c.targets == 0) {

                    Node gut = new Node("*", new Node[]{
                            new Node("-1"),
                            c
                    });
                    gut.changed = true;

                    Node temp = new Node("+", new Node[]{
                            y.clone(),
                            gut
                    });

                    y.value = temp.value;
                    y.nodes = temp.nodes;

                    x.nodes.remove(i);

                    return true;
                }
            }
        }

        if (y.value.equals("+")) {
            // go through x and find any non targets to push away
            for (int i = 0; i < y.nodes.size(); i++) {
                Node c = y.nodes.get(i);

                if (c.targets > 0) {

                    Node gut = new Node("*", new Node[]{
                            new Node("-1"),
                            c
                    });
                    gut.changed = true;

                    Node temp = new Node("+", new Node[]{
                            x.clone(),
                            gut
                    });

                    x.value = temp.value;
                    x.nodes = temp.nodes;

                    y.nodes.remove(i);

                    return true;
                }
            }
        }

        return false;
    }

    public static boolean Step(Node eq, String target, boolean e) {
        Node last = eq.clone();

        Parser.MarkUp(eq, target);
        if (!Simplify.Step(eq)) return false;
        Solve(eq, target);

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
            if (Step(n, target)) break;
            Parser.MarkUp(n);
            System.out.println(Parser.ReadNode(n));
        }
        Parser.MarkUp(n);
        System.out.println(Parser.ReadNode(n));
        System.out.println("Done... " + i);
    }
}
