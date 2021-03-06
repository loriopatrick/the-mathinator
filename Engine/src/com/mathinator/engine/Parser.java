package com.mathinator.engine;

import java.util.ArrayList;

import com.mathinator.engine.Bools;
import com.mathinator.engine.Node;

public class Parser {

    public static int type(char c) {
        int i = (int) c;
        if ((i >= 48 && i <= 57) || i == 46) return 1; // Number
        if (i >= 97 && i <= 122) return 2; // Letter
        switch (i) {
            case 42:
            case 43:
            case 45:
            case 47:
            case 94:
            case 61:
            case (int) ')':
            case (int) '(':
            case (int) ',':
                return 3; // Operator
        }
        if (i >= 65 && i <= 90) return 4; // Cap letter
        return 0;
    }

    public static String[] Block(String eq) {
        eq = eq.replace(")-", ")+-"); // put into parse
        ArrayList<String> res = new ArrayList<String>();
        StringBuilder temp = new StringBuilder();
        boolean neg = false;
        int last2 = 0;
        int last = -1;
        for (int i = 0; i < eq.length(); i++) {
            char c = eq.charAt(i);
            int type = type(c);
            if (c == 45 || c == 8722) {
                neg = true;
            } else if (type == 3) {
                if (temp.length() > 0) res.add(temp.toString());
                temp.setLength(0);
                res.add(c + "");
                type = -1;
            } else if (type == last) {
                temp.append(c);
            } else {
                if (temp.length() > 0) res.add(temp.toString());
                temp.setLength(0);
                if (neg) {
                    if (last2 == 1 || last2 == 2) {
                        res.add("+");
                    }
                    temp.append('-');
                    neg = false;
                }
                temp.append(c);
            }
            last2 = last;
            last = type;
        }
        if (temp.length() > 0) res.add(temp.toString());
        String[] Res = new String[res.size()];
        res.toArray(Res);
        return Res;
    }

    public static Node Parse(String[] eq, int start) throws Exception {
        Node current;

        int count = 0;
        for (String c : eq) {
            if (c.equals("(")) ++count;
            if (c.equals(")")) --count;
        }

        if (count != 0) return null;

        if (eq[start].equals("(")) {
            current = Parse(eq, start + 1);
            start = current.temp;
        } else {
            current = new Node(eq[start]);
        }

        for (int i = start + 1; i < eq.length; i++) {
            String s = eq[i];
            char c = s.charAt(0);

            if (c == '(') {
                Node res = Parse(eq, i + 1);
                current.nodes.add(res);
                res.parent = current;
                i = res.temp;
            } else if (c == ')') {
                while (current.parent != null) current = current.parent;
                current.temp = i;
                return current;
            } else if (c == '^' || c == '/') {
                Node temp = new Node(s);
                if (current.nodes.size() > 0 && current.temp == -1) {
                    temp.nodes.add(current.nodes.get(current.nodes.size() - 1));
                    current.nodes.set(current.nodes.size() - 1, temp);
                    temp.parent = current;
                    current = temp;
                } else {
                    temp.nodes.add(current);
                    current.parent = temp;
                    current = temp;
                }
            } else if (Bools.isFn(s)) {
                Node temp = new Node(s);
                current.nodes.add(temp);
                temp.parent = current;
                current = temp;
            } else if (c == '+' || c == ';') {

                Node temp = current;
                boolean go = false;

                for (; ; ) {
                    if (temp.value.charAt(0) == c) {
                        go = true;
                        current = temp;
                        break;
                    }
                    if (temp.parent == null) break;
                    temp = temp.parent;
                }

                if (!go) {
                    current = temp;
                    temp = new Node(s.equals(";")? "," : s);
                    temp.nodes.add(current);
                    current.parent = temp;
                    current = temp;
                }

            } else if (c == '*') {

                Node temp = current;
                boolean go = false;

                for (; ; ) {
                    if (temp.value.charAt(0) == '*') {
                        current = temp;
                        go = true;
                        break;
                    }
                    if (temp.value.charAt(0) == '+' && temp.temp == -1) {
                        current = temp;
                        temp = new Node(s);
                        temp.nodes.add(current.nodes.get(current.nodes.size() - 1));
                        current.nodes.set(current.nodes.size() - 1, temp);
                        temp.parent = current;
                        current = temp;
                        go = true;
                        break;
                    }
                    if (temp.parent == null) break;
                    temp = temp.parent;
                }

                if (!go) {
                    current = temp;
                    temp = new Node(s);
                    temp.nodes.add(current);
                    current.parent = temp;
                    current = temp;
                }
            } else {
                current.nodes.add(new Node(s));
            }
        }

        while (current.parent != null) current = current.parent;
        return current;
    }

    public static Node Parse(String[] eq) throws Exception {
        return Parse(eq, 0);
    }

    public static void MarkUp(Node n, String target) {
        n.targets = 0;
        n.unknowns = 0;
        if (n.nodes.size() == 0) {
            boolean t = n.value.equals(target) || n.value.equals("-" + target);
            boolean k = n.nodes.size() == 0 && !Bools.isNum(n.value) && !Bools.isFn(n.value);
            n.targets = 0;
            n.unknowns = 0;
            Node temp = n;
            temp.height = 0;
            while (temp != null) {
                if (t) temp.targets++;
                if (k) temp.unknowns++;
                if (temp.parent != null) {
                    if (temp.parent.height < temp.height + 1)
                        temp.parent.height = temp.height + 1;
                }
                temp = temp.parent;
            }
            return;
        }

        for (int i = 0; i < n.nodes.size(); i++) {
            Node c = n.nodes.get(i);
            c.parent = n;
            c.targets = 0;
            c.unknowns = 0;
            c.height = 0;
            MarkUp(c, target);
        }
    }

    public static void MarkUp(Node n) {
        MarkUp(n, "x");
    }

    public static Node CreateNode(String eq, String target) throws Exception {
        String[] masterParts = eq.split(",");
        if (masterParts.length > 1) {
            Node res = new Node(",");
            for (String part : masterParts) {
                res.nodes.add(CreateNode(part, target));
            }

            return res;
        }

        String[] parts = eq.split("=");
        if (parts.length == 1) {
            String[] block = Block(eq);
            Node n = Parse(block);
            if (target != null)
                MarkUp(n, target);
            return n;
        } else if (parts.length == 2) {
            if (target == null) throw new Exception("has \"=\", needs variable");
            Node left = Parse(Block(parts[0])),
                    right = Parse(Block(parts[1]));
            Node res = new Node("=", new Node[]{
                    left, right
            });
            left.parent = res;
            right.parent = res;
            MarkUp(res, target);
            return res;
        }
        return null;
    }

    public static Node CreateNode(String eq) throws Exception {
        return CreateNode(eq, "  ");
    }

    // for internal use only
    public static String ReadNode(Node node) {
        if (node.height == 0 || node.nodes.size() == 0) {
            if (node.value.equals(Math.PI + "")) {
                return "pi";
            }
            return node.value;
        }
        StringBuilder sb = new StringBuilder();
        boolean fn = false;
        if (Bools.isFn(node.value)) {
            fn = true;
            sb.append(node.value);
            sb.append('(');
        }
        for (int i = 0; i < node.nodes.size(); i++) {
            if (node.nodes.get(i).height != 0) {
                sb.append('(');
                sb.append(ReadNode(node.nodes.get(i)));
                sb.append(')');
            } else {
                sb.append(ReadNode(node.nodes.get(i)));
            }
            if (i < node.nodes.size() - 1) {
                if (!(node.value.equals("+") && node.nodes.get(i + 1).value.startsWith("-")))
                    sb.append(node.value);
            }
        }
        if (fn) {
            sb.append(')');
        }
        return sb.toString();
    }

    public static String ReadNodeLatex(Node node) {

        StringBuilder sb = new StringBuilder();

        if (node.changed) {
            sb.append("\\cl selected{");
        }

        if (node.height == 0) {
            if (node.valEquals("pi")) {
                sb.append("\\pi");
            } else if (Bools.isNum(node.value)
                    && node.value.indexOf(".0") == node.value.length() - 2) {
                sb.append(node.value.replace(".0", ""));
            } else {
                sb.append(node.value);
            }
            if (node.changed) sb.append('}');
            return sb.toString();
        }

        if (node.valEquals("d")) {
            Node n = node.nodes.get(0),
                    target = new Node("x");

            if (n.valEquals(",")) {
                target = n.nodes.get(1);
                n = n.nodes.get(0);
            }

            sb.append("{\\partial}/{\\partial ").append(target.value).append('}');
            sb.append('(').append(ReadNodeLatex(n)).append(')');
            if (node.changed) sb.append('}');
            return sb.toString();
        }

        if (Bools.isFn(node.value)) {
            sb.append("\\").append(node.value).append('{');
            sb.append(ReadNodeLatex(node.nodes.get(0)));
            sb.append("}");
            if (node.changed) sb.append('}');
            return sb.toString();
        }


        if (node.value.equals("/")) {
            sb.append("{");
            sb.append(ReadNodeLatex(node.nodes.get(0)));
            sb.append("}/{");
            sb.append(ReadNodeLatex(node.nodes.get(1)));
            sb.append("}");
            if (node.changed) sb.append('}');
            return sb.toString();
        }

        if (node.value.equals("^")) {
            if (node.nodes.get(1).value.equals("/") && node.nodes.get(1).nodes.get(0).value.equals("1")
                    && node.nodes.get(1).nodes.get(1).height == 0) {
                if (!node.nodes.get(1).nodes.get(1).value.equals("2")) {
                    sb.append("\\cl pow {").append(ReadNodeLatex(node.nodes.get(1).nodes.get(1))).append('}');
                }
                sb.append("\\sqrt");
                sb.append('{').append(ReadNodeLatex(node.nodes.get(0))).append('}');
            } else {
                sb.append("{");
                sb.append(ReadNodeLatex(node.nodes.get(0)));
                sb.append("}^{");
                sb.append(ReadNodeLatex(node.nodes.get(1)));
                sb.append("}");
            }
            if (node.changed) sb.append('}');
            return sb.toString();
        }


        boolean p = node.parent != null && (node.value.equals("+") && node.parent.value.equals("*")
                || node.parent.value.equals("^"));

        if (p) sb.append('(');

        for (int i = 0; i < node.nodes.size(); i++) {
            if (node.nodes.get(i).height != 0) {
                sb.append('{');
                sb.append(ReadNodeLatex(node.nodes.get(i)));
                sb.append('}');
            } else {
                sb.append(ReadNodeLatex(node.nodes.get(i)));
            }
            if (i < node.nodes.size() - 1) {
                if (!(node.value.equals("+") && node.nodes.get(i + 1).value.startsWith("-"))) {

                    if (node.valEquals("*")) {

                        Node n = node.nodes.get(i), nx = node.nodes.get(i + 1);

                        if (n.valEquals("^") && nx.valEquals("^")) {
                        } else if (nx.valEquals("^") && nx.nodes.get(1).valEquals("/")
                                && nx.nodes.get(1).nodes.get(0).valEquals("1")) {
                        } else if (nx.valEquals("+")) {
                        } else if (!n.valEquals("+") && !n.valEquals("^") && nx.height == 0 && !Bools.isNum(nx.value)) {
                        } else {
                            sb.append(node.value);
                        }
                    } else {
                        sb.append(node.value);
                    }

                }
            }
        }

        if (p) sb.append(')');
        if (node.changed) sb.append('}');
        return sb.toString();
    }
}
