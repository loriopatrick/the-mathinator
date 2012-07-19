package com.mathenator.engine;

import java.util.ArrayList;

public class Solve2 {

    public static boolean Solve(Node node, String target) {
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

        if (node.temp != -10) {
            if (Adds(x, y, node, toLeft)) return true;
            if (Divs(x, y, node, toLeft)) return true;
            if (Multis(x, y, node, toLeft)) return true;
            if (Pows(x, y, node, toLeft)) return true;
        }

        if (Factor(x, y, node, toLeft, target)) return true;

        if ("a".equals("b")) return false;

        return false;
    }

    public static boolean Factor(Node x, Node y, Node eq, boolean toLeft, String target) {

        if (!Bools.isNum(y.value) || Float.parseFloat(y.value) != 0) {
            eq.temp = -10;

            if (x.value.equals("+")) {
                Node temp = new Node("*", new Node[] {
                        new Node("-1"),
                        y.clone()
                });
                temp.changed = true;

                x.nodes.add(temp);
                y.value = "0";
                y.nodes.clear();

                return true;
            }

            Node guts = new Node("*", new Node[] {
                    new Node("-1"),
                    y
            });
            guts.changed = true;

            Node temp = new Node("+", new Node[] {
                    x,
                    guts
            });

            eq.nodes.set(toLeft? 0 : 1, temp);
            y.value = "0";
            y.nodes.clear();

            return true;
        }

        ArrayList<Node> powers = new ArrayList<Node>();

        if (x.value.equals("+")) {
            for (int i = 0; i < x.nodes.size(); i++) {
                Node c = x.nodes.get(i);
                if (c.value.equals("^")) {
                    if (c.nodes.get(0).value.equals(target)) {
                        powers.add(c.nodes.get(1));
                    }
                } else if (c.value.equals(target)) {
                    
                }
            }
        }

        return false;
    }

    public static boolean Multis(Node x, Node y, Node eq, boolean toLeft) {
        if (y.targets == 0 && x.value.equals("*")) {
            for (int i = 0; i < x.nodes.size(); i++) {
                Node c = x.nodes.get(i);
                if (c.targets == 0) {
                    c.changed = true;
                    Node temp = new Node("/", new Node[]{
                            y,
                            c
                    });

                    eq.nodes.set(toLeft ? 1 : 0, temp);

                    x.nodes.remove(i);

                    return true;
                }
            }
        } else if (y.value.equals("*")) {
        }
        return false;
    }

    public static boolean Pows(Node x, Node y, Node eq, boolean toLeft) {

        if (x.value.equals("^") && y.value.equals("^")) {
            if (x.nodes.get(1).equals(y.nodes.get(1))) {
                x.value = x.nodes.get(0).value;
                x.nodes = x.nodes.get(0).nodes;

                y.value = y.nodes.get(0).value;
                y.nodes = y.nodes.get(0).nodes;

                return true;
            }
        }

        if (x.value.equals("^") && x.nodes.get(1).targets == 0) {

            Node temp;

            if (x.nodes.get(1).value.equals("/")) {
                Node d = x.nodes.get(1).nodes.get(1);
                d.changed = true;

                temp = new Node("^", new Node[]{
                        y,
                        d.clone()
                });

                d.value = "1";
                d.nodes.clear();
            } else {
                Node guts = new Node("/", new Node[]{
                        new Node("1"),
                        x.nodes.get(1)
                });
                guts.changed = true;

                temp = new Node("^", new Node[]{
                        y,
                        guts
                });

                x.nodes.set(1, new Node("1"));
            }

            eq.nodes.set(toLeft ? 1 : 0, temp);
            return true;
        }


        if (y.value.equals("^") && y.targets > 0 && y.nodes.get(1).targets == 0) {

            Node temp;

            if (y.nodes.get(1).value.equals("/")) {
                Node d = y.nodes.get(1).nodes.get(1);
                d.changed = true;

                temp = new Node("^", new Node[]{
                        x,
                        d.clone()
                });

                d.value = "1";
                d.nodes.clear();
            } else {
                Node guts = new Node("/", new Node[]{
                        new Node("1"),
                        y.nodes.get(1)
                });
                guts.changed = true;

                temp = new Node("^", new Node[]{
                        x,
                        guts
                });

                y.nodes.set(1, new Node("1"));
            }

            eq.nodes.set(toLeft ? 0 : 1, temp);
            return true;
        }

        return false;
    }

    public static boolean Divs(Node x, Node y, Node eq, boolean toLeft) {
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
                Node temp = new Node("*", new Node[]{
                        x.nodes.get(0),
                        y.nodes.get(1)
                });

                Node temp2 = new Node("*", new Node[]{
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

        if (x.value.equals("/")) {
            Node temp = new Node("*", new Node[]{
                    y,
                    x.nodes.get(1)
            });
            x.nodes.get(1).changed = true;
            eq.nodes.set(toLeft ? 1 : 0, temp);
            x.nodes.set(1, new Node("1"));

            return true;
        }
        return false;
    }

    public static boolean Adds(Node x, Node y, Node eq, boolean toLeft) {

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
                            y,
                            gut
                    });

                    eq.nodes.set(toLeft ? 1 : 0, temp);

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
                            x,
                            gut
                    });

                    eq.nodes.set(toLeft ? 0 : 1, temp);

                    y.nodes.remove(i);

                    return true;
                }
            }
        }

        if (y.targets > 0 && y.height < 2) {
            Node guts = new Node("*", new Node[]{
                    new Node("-1"),
                    y.clone()
            });
            guts.changed = true;

            Node temp = new Node("+", new Node[]{
                    x,
                    guts
            });

            eq.nodes.set(toLeft ? 0 : 1, temp);
            y.value = "0";
            y.nodes.clear();

            return true;
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
