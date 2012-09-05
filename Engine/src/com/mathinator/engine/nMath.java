package com.mathinator.engine;

import java.util.ArrayList;
import java.util.Collections;

public class nMath {

    public static ArrayList<Node> Multiples(Node n) {
        ArrayList<Node> res = null;
        if (n.valEquals("*")) {
            res = new ArrayList<Node>();
            for (Node c : n.nodes) {
                ArrayList<Node> temp = Multiples(c);
                if (temp == null) continue;
                res.addAll(temp);
            }
            if (res.size() == 0) return null;
        } else if (Bools.isNum(n.value)) {
            ArrayList<Integer> mults = Multiples(n.value);
            if (mults == null) return res;
            res = new ArrayList<Node>();
            for (int i : mults) {
                res.add(new Node(i + ""));
            }
        } else {
            res = new ArrayList<Node>();
            res.add(n);
        }
        return res;
    }

    public static void DestroyCommon(ArrayList a, ArrayList b) {
        for (int i = 0; i < a.size(); ) {
            boolean remove = false;
            for (int j = 0; j < b.size(); ) {
                if (a.get(i).equals(b.get(j))) {
                    remove = true;
                    a.remove(i);
                    b.remove(j);
                    continue;
                }
                ++j;
            }
            if (!remove) ++i;
        }
    }

    public static ArrayList<Integer> Multiples(float v) {
        boolean neg = v < 0;
        if (neg) v *= -1;

        if (Math.floor(v) != v) return null;
        ArrayList<Integer> res = new ArrayList<Integer>();
        for (int i = 2; i < v; i++) {
            if (v % i == 0) {
                res.add(i);
                float n = (v / i);
                res.addAll(Multiples(n));
                Collections.sort(res);
                if (neg && res.size() > 0) {
                    res.set(0, res.get(0) * -1);
                }
                return res;
            }
        }
        if (res.size() == 0) {
            res.add(1);
            res.add((int) v);
        }
        Collections.sort(res);
        if (neg && res.size() > 0) {
            res.set(0, res.get(0) * -1);
        }
        return res;
    }

    public static ArrayList<Integer> Multiples(String v) {
        return Multiples(Float.parseFloat(v));
    }

    public static int[] SimplifyFraction(float n, float d) {
        if (Math.floor(n) != n) return null;
        if (Math.floor(d) != d) return null;

        Integer one = new Integer(1);

        ArrayList<Integer> nM = Multiples(n);
        while (nM.contains(one)) nM.remove(one);
        ArrayList<Integer> dM = Multiples(d);
        while (dM.contains(one)) dM.remove(one);

        for (int i = 0; i < nM.size(); ) {
            if (nM.size() == 0) break;
            boolean in = false;
            for (int j = 0; j < dM.size(); ) {
                if (dM.size() == 0 || nM.size() == 0) break;
                if (i < nM.size() && nM.get(i).equals(dM.get(j))) {
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
                    if (multi != null) {
                        multi.remove(new Integer(1));
                        if (multi.size() > 0) {
                            added = true;
                            for (int j = 0; j < multi.size(); ++j) {
                                result.add(new Node(multi.get(j) + ""));
                            }
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
                if (multi != null) {
                    multi.remove(new Integer(1));
                    if (multi != null && multi.size() > 0) {
                        added = true;
                        for (int j = 0; j < multi.size(); ++j) {
                            result.add(new Node(multi.get(j) + ""));
                        }
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

    public static int Negatives(Node n) {
        int sum = 0;
        if (n.nodes.size() == 0) {
            return n.value.charAt(0) == '-' ? 1 : 0;
        }

        if (n.valEquals("+")) return -1;

        for (int i = 0; i < n.nodes.size(); ++i) {
            int v = Negatives(n.nodes.get(i));
            if (v == -1) return -1;
            sum += v;
        }

        return sum;
    }

    public static boolean isNegative(Node n) {
        int res = Negatives(n);
        if (res == -1) return false;
        return res % 2 == 1;
    }

    public static float Round(float Rval, int Rpl) {
        float p = (float) Math.pow(10, Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float) tmp / p;
    }
}
