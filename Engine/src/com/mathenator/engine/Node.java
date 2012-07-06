package com.mathenator.engine;

import java.util.ArrayList;
import java.util.Collections;

public class Node {

    public Node(String value) {
        this.value = value;
        nodes = new ArrayList<Node>();
    }

    public Node(String value, Node[] nodes) {
        this.value = value;
        this.nodes = new ArrayList<Node>(nodes.length);
        Collections.addAll(this.nodes, nodes);
    }

    public boolean equals(Node compare) {
        if (Bools.isNum(this.value) && Bools.isNum(compare.value)) {
            if (Float.parseFloat(this.value) != Float.parseFloat(compare.value)) return false;
        } else if (!this.value.equals(compare.value)) return false;

        if (this.value.equals("+") || this.value.equals("*")) {
            if (this.nodes.size() != compare.nodes.size()) return false;
            for (Node child : this.nodes) {
                boolean in = false;
                for (Node child2 : compare.nodes) {
                    if (child.equals(child2)) {
                        in = true;
                        break;
                    }
                }
                if (!in) return false;
            }
        } else if (this.value.equals("/") || this.value.equals("^") || this.value.equals("=")) {
            if (!this.nodes.get(0).equals(compare.nodes.get(0))) return false;
            if (!this.nodes.get(1).equals(compare.nodes.get(1))) return false;
        }

        return true;
    }

    public Node clone() {
        Node res = new Node(this.value);
        for (int i = 0; i < this.nodes.size(); i++) {
            res.nodes.add(this.nodes.get(i).clone());
        }
        return res;
    }

    public void changeTo(Node n) {
        this.value = n.value;
        this.nodes = n.nodes;
    }

    public String value;
    public ArrayList<Node> nodes;
    public Node parent;

    public int targets = 0;
    public int height = -1;
    public boolean simple = false;
    public int temp = -1;
}