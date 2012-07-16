package com.mathenator.engine;


public class Factor {

    public static void Factor (Node node) {
        if (node.value.equals("+")) {
            if (node.nodes.size() % 2 == 0) {
                for (int i = 0; i < node.nodes.size(); i++) {
                    Node a = node.nodes.get(i);
                    for (int j = 0; j < node.nodes.size(); j++) {
                        if (i == j) continue;
                        Node b = node.nodes.get(j);

                        // 5x^2 + 3x + 9x^2 + 5x
                        // 14x^2 + 8x
                    }
                }
            }
        }
    }

}
