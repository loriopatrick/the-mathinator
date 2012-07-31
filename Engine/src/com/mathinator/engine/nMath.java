package com.mathinator.engine;

import java.util.ArrayList;

import com.mathinator.engine.Bools;
import com.mathinator.engine.Node;
import com.mathinator.engine.nMath;

public class nMath {

    public static ArrayList<Integer> Multiples(float v) {
        if (Math.floor(v) != v) return null;
        ArrayList<Integer> res = new ArrayList<Integer>();
        for (int i = 2; i < v; i++) {
            if (v % i == 0) {
                res.add(i);
                float n = (v / i);
                res.addAll(Multiples(n));
                return res;
            }
        }
        if (res.size() == 0) {
            res.add(1);
            res.add((int) v);
        }
        return res;
    }

    public static ArrayList<Integer> Multiples(String v) {
        return Multiples(Float.parseFloat(v));
    }

    public static int[] SimplifyFraction(float n, float d) {
        if (Math.floor(n) != n) return null;
        if (Math.floor(d) != d) return null;

        ArrayList<Integer> nM = Multiples(n);
        ArrayList<Integer> dM = Multiples(d);

        for (int i = 0; i < nM.size(); ) {
            if (nM.size() == 0) break;
            boolean in = false;
            for (int j = 0; j < dM.size(); ) {
                if (dM.size() == 0 || nM.size() == 0) break;
                if (nM.get(i).equals(dM.get(j))) {
                    in = true;
                    nM.remove(i);
                    dM.remove(j);
                    continue;
                }
                j++;
            }
            if (!in) i++;
        }

        int N = 1;
        for (int i : nM) {
            N *= i;
        }

        int D = 1;
        for (int i : dM) {
            D *= i;
        }

        return new int[]{N, D};
    }

    public static ArrayList<Node> Commons(Node node) {
        ArrayList<Node> result = new ArrayList<Node>();
        if (node.value.equals("/")) node = node.nodes.get(0);
        if (node.value.equals("*")) {
            for (int i = 0; i < node.nodes.size(); i++) {

                Node n = node.nodes.get(i);
                boolean added = false;
                if (Bools.isNum(n.value)) {
                    ArrayList<Integer> multi = nMath.Multiples(n.value);
                    multi.remove(new Integer(1));
                    if (multi != null && multi.size() > 0) {
                        added = true;
                        for (int j = 0; j < multi.size(); ++j) {
                            result.add(new Node(multi.get(j) + ""));
                        }
                    }
                }

                if (!added) {
                    if (n.value.equals("^")) {
                        result.add(n.nodes.get(0));
                    } else {
                        result.add(n);
                    }
                }
            }
        } else if (node.value.equals("+")) {
            result = Commons(node.nodes.get(0));
            for (int i = 1; i < node.nodes.size(); i++) {
                ArrayList<Node> temp = Commons(node.nodes.get(i));

                EliminateNon(result, temp);

            }
        } else if (node.value.equals("^")) {
            result.add(node.nodes.get(0));
        } else {
            boolean added = false;
            if (Bools.isNum(node.value)) {
                ArrayList<Integer> multi = nMath.Multiples(node.value);
                multi.remove(new Integer(1));
                if (multi != null && multi.size() > 0) {
                    added = true;
                    for (int j = 0; j < multi.size(); ++j) {
                        result.add(new Node(multi.get(j) + ""));
                    }
                }
            }

            if (!added) result.add(node);
        }
        return result;
    }

    public static void EliminateNon(ArrayList<Node> base, ArrayList<Node> compare) {
        for (int j = 0; j < base.size(); ) {
            int pos = -1;
            for (int k = 0; k < compare.size(); ++k) {
                if (compare.get(k).equals(base.get(j))) {
                    pos = k;
                    break;
                }
            }

            if (pos == -1) {
                base.remove(j);
            } else {
                compare.remove(pos);
                ++j;
            }
        }
    }
}
