import java.util.ArrayList;

public class nMath {

    public static ArrayList<Integer> Multiples (float v) {
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
            res.add((int)v);
        }
        return res;
    }

    public static int[] SimplifyFraction (float n, float d) {
        if (Math.floor(n) != n) return null;
        if (Math.floor(d) != d) return null;

        ArrayList<Integer> nM = Multiples(n);
        ArrayList<Integer> dM = Multiples(d);

        for (int i = 0; i < nM.size();) {
            boolean in = false;
            for (int j = 0; j < dM.size();) {
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

        return new int[] {N, D};
    }
}
