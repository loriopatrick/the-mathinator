public class Solve {
    public static void Step(Node eq, String target) {
        if (!eq.value.equals("=")) return;

        Parser.MarkUp(eq, target);
        if (eq.targets == 0) return;

        Node left = eq.nodes.get(0),
                right = eq.nodes.get(1);

        boolean toLeft = eq.nodes.get(0).targets >= eq.nodes.get(1).targets;

        if (left.value.equals("+") && right.value.equals("+")) {
            for (int i = 0; i < left.nodes.size(); i++) {
                Node c = left.nodes.get(i);
                boolean go = false;

                if (toLeft && c.targets == 0) go = true;
                else if (c.targets > 0) go = true;

                if (go) {
                    right.nodes.add(new Node("*", new Node[]{
                            new Node("-1"),
                            c
                    }));
                    left.nodes.remove(i);
                    return;
                }
            }

            for (int i = 0; i < right.nodes.size(); i++) {
                Node c = right.nodes.get(i);
                boolean go = false;

                if (toLeft && c.targets > 0) go = true;
                else if (c.targets == 0) go = true;

                if (go) {
                    left.nodes.add(new Node("*", new Node[]{
                            new Node("-1"),
                            c
                    }));
                    right.nodes.remove(i);
                    return;
                }
            }
        } else if (left.value.equals("+")) {
            for (int i = 0; i < left.nodes.size(); i++) {
                Node c = left.nodes.get(i);
                boolean go = false;

                if (toLeft && c.targets == 0) go = true;
                else if (c.targets > 0) go = true;

                if (go) {
                    eq.nodes.set(1, new Node("+", new Node[]{
                            eq.nodes.get(1),
                            new Node("*", new Node[]{
                                    new Node("-1"),
                                    c
                            })
                    }));
                    left.nodes.remove(i);
                    return;
                }
            }
        } else if (right.value.equals("+")) {
            for (int i = 0; i < right.nodes.size(); i++) {
                Node c = right.nodes.get(i);
                boolean go = false;

                if (toLeft && c.targets > 0) go = true;
                else if (c.targets == 0) go = true;

                if (go) {
                    eq.nodes.set(0, new Node("+", new Node[]{
                            eq.nodes.get(0),
                            new Node("*", new Node[]{
                                    new Node("-1"),
                                    c
                            })
                    }));
                    right.nodes.remove(i);
                    return;
                }
            }
        }
    }

    public static boolean Run(Node eq, String target) {
        Node last = eq.clone();
        Simplify.Simplify(eq);

        if (!eq.equals(last)) return false;

        Solve.Step(eq, target);

        return eq.equals(last);
    }
}
