package com.mathinator.engine;

import java.util.ArrayList;
import java.util.Collections;

import com.mathinator.engine.Bools;
import com.mathinator.engine.Node;
import com.mathinator.engine.Parser;

public class Node {

    public Node(String value) {
        this.value = value;
        nodes = new ArrayList<Node>();
    }

    public Node(String value, boolean changed) {
        this.value = value;
        this.changed = changed;
        nodes = new ArrayList<Node>();
    }

    public Node(String value, Node[] nodes) {
        this.value = value;
        this.nodes = new ArrayList<Node>(nodes.length);
        Collections.addAll(this.nodes, nodes);
    }

    public Node(String value, Node[] nodes, boolean changed) {
        this.value = value;
        this.changed = changed;
        this.nodes = new ArrayList<Node>(nodes.length);
        Collections.addAll(this.nodes, nodes);
    }

    public boolean equals(Node compare) {
        if (compare.value.equals("ANY")) return true;
        if (Bools.isNum(this.value) && Bools.isNum(compare.value)) {
            if (Float.parseFloat(this.value) != Float.parseFloat(compare.value)) return false;
        } else if (!this.value.equals(compare.value)) return false;

        if (this.value.equals("+") || this.value.equals("*") || this.value.equals("=") || this.value.equals(",")) {
            if (this.nodes.size() != compare.nodes.size()) return false;
            ArrayList<Integer> used = new ArrayList<Integer>();
            for (Node child : this.nodes) {
                boolean in = false;
                for (int i = 0; i < compare.nodes.size(); ++i) {
                    if (used.contains(i)) continue;
                    if (child.equals(compare.nodes.get(i))) {
                        used.add(i);
                        in = true;
                        break;
                    }
                }
                if (!in) return false;
            }
        } else if (this.value.equals("/") || this.value.equals("^")) {
            if (!this.nodes.get(0).equals(compare.nodes.get(0))) return false;
            if (!this.nodes.get(1).equals(compare.nodes.get(1))) return false;
        }

        return true;
    }

    public Node clone(boolean changed) {
        Node res = new Node(this.value);
        res.changed = changed;
        for (int i = 0; i < this.nodes.size(); i++) {
            res.nodes.add(this.nodes.get(i).clone(changed));
        }
        return res;
    }

    public Node clone() {
        return clone(false);
    }

    public boolean valEquals (String v) {
        if (v.equalsIgnoreCase(this.value)) return true;
        if (Bools.isNum(v) && Bools.isNum(this.value)
                && Float.parseFloat(v) == Float.parseFloat(this.value)) return true;
        return false;
    }

    public void clone (Node node) {
        this.value = node.value;
        this.nodes = node.nodes;
        this.changed = node.changed;
    }

    public boolean contains (Node node) {
        if (this.equals(node)) return true;
        for (Node n : this.nodes) {
            if (n.contains(node)) return true;
        }
        return false;
    }

    public int find (Node node, int layer) {
        if (this.equals(node)) return ++layer;
        for (Node n : this.nodes) {
            int l;
            if ((l = n.find(node, layer)) > -1) return l;
        }
        return -1;
    }

    public int find (Node node) {
        return this.find(node, -1);
    }

    public String toString () {
        return Parser.ReadNode(this);
    }

    public String value;
    public ArrayList<Node> nodes;
    public Node parent;

    public int targets = 0;
    public int height = -1;
    public boolean simple = false;
    public int temp = -1;
    public boolean changed = false;
    public String message;
}