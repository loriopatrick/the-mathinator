
public class Simplify {
    public static boolean Simplify (Node node) {
        if (node.height == 0) {
            node.simple = true;
            return false;
        }
        if (node.nodes.size() == 1) {
            node.value = node.nodes.get(0).value;
            node.nodes.clear();
            return false;
        }
        if (node.parent != null && node.value.equals(node.parent.value)) {
            node.parent.nodes.remove(node);
            node.parent.nodes.addAll(node.nodes);
            node.parent.simple = false;
            return false;
        }
        if (node.value.equals("+")) {
            float sum = 0;
            int nAction = 0;
            int size = node.nodes.size();
            for (int i = 0; i < node.nodes.size();) {
                Node a = node.nodes.get(i);
                boolean addI = false;
                for (int j = 0; j < node.nodes.size();) {
                    if (i == j) {
                        ++j;
                        ++nAction;
                        continue;
                    }

                    Node b = node.nodes.get(j);

                    if (isNum(a.value) && isNum(b.value)) {
                        sum = Float.parseFloat(a.value) + Float.parseFloat(b.value);

                        if (i > j) {
                            node.nodes.remove(i);
                            node.nodes.set(j, new Node(sum + ""));
                            ++j;
                        } else {
                            node.nodes.remove(j);
                            node.nodes.set(i, new Node(sum + ""));
                            addI = true;
                        }

                    } else if (a.equals(b)) {
                        Node temp = new Node("*", new Node[] {
                                new Node("2"),
                                a
                        });
                        if (i > j) {
                            node.nodes.remove(i);
                            node.nodes.set(j, temp);
                            ++j;
                        } else {
                            node.nodes.remove(j);
                            node.nodes.set(i, temp);
                            addI = true;
                        }
                    } else if (a.value.equals("*") && b.height == 0) {

                    } else {
                        ++nAction;
                    }
                }
                if (addI) ++i;
            }
            if (nAction == size && size == node.nodes.size())
                node.simple = true;
            return true;
        }
        return false; // Simplify if movement took place
    }
    public static boolean Run (Node node) {
        Node current = node;
        int state = 0;

        for (;;) {
            if (current.simple) {
                if (current == node) return true;
                current = current.parent;
            } else {
                boolean simple = true;
                for (int i = 0; i < current.nodes.size(); i++) {
                    Node c = current.nodes.get(i);
                    if (c.simple) continue;
                    simple = false;
                    current = c;
                    break;
                }
                if (simple) {
                    if(Simplify(current)) return false;
                }
            }
        }
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
}
