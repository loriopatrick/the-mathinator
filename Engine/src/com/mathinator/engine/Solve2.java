package com.mathinator.engine;

import java.rmi.MarshalledObject;

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
            if (Divs(x, y, node, toLeft)) return true;
            if (Adds(x, y, node, toLeft)) return true;
            if (Multis(x, y, node, toLeft)) return true;
            if (Pows(x, y, node, toLeft)) return true;
        }

        if (xNom(x, y, target)) return true;

        if (Factor(x, y, node, toLeft, target)) return true;

        if ("a".equals("b")) return false;

        return false;
    }

    public static boolean xNom(Node x, Node y, String target) {
        if (y.valEquals("0") && x.valEquals("+")) {
            boolean good = false;
            for (Node c : x.nodes) {
                if (!c.valEquals("/")) continue;
                if (c.nodes.get(1).targets <= 0) continue;
                good = true;
                break;
            }

            if (good) {
                Node temp = new Node(target);
                for (Node c : x.nodes) {
                    if (c.valEquals("*")) {
                        c.nodes.add(temp.clone(true));
                    } else {
                        c.clone(new Node("*", new Node[]{
                                temp.clone(true),
                                c.clone()
                        }));
                    }
                }

                return true;
            }
        }
        return false;
    }

    public static boolean PreSolve(Node node, String target) {
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

        if (Break(x, y, node)) return true;

        return false;
    }

    public static boolean Break(Node x, Node y, Node eq) {
        if (y.valEquals("0") && x.valEquals("*")) {
            Node temp = new Node(",");

            for (int i = 0; i < x.nodes.size(); ++i) {
                if (x.nodes.get(i).targets == 0) continue;
                temp.nodes.add(new Node("=", new Node[]{
                        x.nodes.get(i),
                        new Node("0")
                }));
            }

            if (temp.nodes.size() > 1) {
                eq.clone(temp);
                return true;
            }
        }
        return false;
    }

    public static boolean Factor(Node x, Node y, Node eq, boolean toLeft, String target) {

        if (eq.temp != -10) {
            if (x.value.equals("+")) {
                boolean good = true;
                for (int i = 0; i < x.nodes.size(); i++) {
                    Node c = x.nodes.get(i);
                    int pos = c.find(new Node(target));
                    if (c.targets == 0
                            || !(c.value.equals(target)
                            || (c.value.equals("*")
                            && c.find(new Node(target)) == 0
                            && c.find(new Node("^", new Node[]{
                            new Node(target),
                            new Node("ANY")
                    })) == -1)
                    )) {
                        good = false;
                        break;
                    }
                }

                if (good) {
                    int pos = toLeft ? 0 : 1;
                    eq.nodes.set(pos, new Node("*", new Node[]{
                            new Node(target),
                            new Node("/", new Node[]{
                                    eq.nodes.get(pos),
                                    new Node(target)
                            })
                    }));
                    return true;
                }
            }
        }

        if (x.value.equals(target)) return false;
        if (!Bools.isNum(y.value) || Float.parseFloat(y.value) != 0) {
            eq.temp = -10;

            if (x.value.equals("+")) {
                Node temp = new Node("*", new Node[]{
                        new Node("-1"),
                        y.clone()
                });
                temp.changed = true;

                x.nodes.add(temp);
                eq.nodes.set(toLeft ? 1 : 0, new Node("0"));

                return true;
            }

            Node guts = new Node("*", new Node[]{
                    new Node("-1"),
                    y
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

        if (!x.value.equals("+")) return false;

        Node[] powers = new Node[5];

        for (int i = 0; i < x.nodes.size(); i++) {
            Node c = x.nodes.get(i);
            if (c.targets == 0) {
                if (powers[0] != null) return false;
                powers[0] = c;
            } else if (c.value.equals("^")) {
                if (c.nodes.get(0).value.equals(target)) {
                    if (!Bools.isNum(c.nodes.get(1).value)) return false;
                    float val = Float.parseFloat(c.nodes.get(1).value);
                    if (val != Math.floor(val)) return false;
                    if (val >= 5) return false;
                    if (val < 0) {
                        for (Node k : x.nodes) {
                            if (k.valEquals("*")) {
                                k.nodes.add(new Node(target, true));
                            } else {
                                k.clone(new Node("*", new Node[] {
                                        new Node(target, true),
                                        k.clone()
                                }));
                            }
                        }
                        return true;
                    }
                    if (powers[(int) val] != null) return false;
                    powers[(int) val] = new Node("1");
                }
            } else if (c.value.equals("*")) {
                boolean have = false;
                for (int j = 0; j < c.nodes.size(); j++) {
                    Node n = c.nodes.get(j);
                    if (n.targets == 0) continue;
                    if (n.value.equals("^")) {
                        if (n.nodes.get(0).value.equals(target)) {
                            if (have) return false;
                            if (!Bools.isNum(n.nodes.get(1).value)) return false;
                            float val = Float.parseFloat(n.nodes.get(1).value);
                            if (val != Math.floor(val)) return false;
                            if (val >= 5) return false;
                            if (val < 0) {
                                for (Node k : x.nodes) {
                                    if (k.valEquals("*")) {
                                        k.nodes.add(new Node(target, true));
                                    } else {
                                        k.clone(new Node("*", new Node[] {
                                                new Node(target, true),
                                                k.clone()
                                        }));
                                    }
                                }
                                return true;
                            }
                            if (powers[(int) val] != null) return false;
                            Node v = c.clone();
                            v.nodes.remove(j);
                            if (v.nodes.size() == 1) {
                                v.clone(v.nodes.get(0));
                            }
                            powers[(int) val] = v;
                            have = true;
                        }
                    } else if (n.value.equals(target)) {
                        if (have) return false;
                        if (powers[1] != null) return false;
                        Node v = c.clone();
                        v.nodes.remove(j);
                        if (v.nodes.size() == 1) {
                            v.clone(v.nodes.get(0));
                        }
                        powers[1] = v;
                        have = true;
                    }
                }
            } else if (c.value.equals(target)) {
                if (powers[1] != null) return false;
                powers[1] = new Node("1");
            }
        }

        for (int i = powers.length - 1; i > 0; --i) {
            if (powers[i] == null) continue;
            if (i == 2) {
                Node n = new Node("*", new Node[]{
                        new Node("+", new Node[]{
                                new Node("x"),
                                new Node("*", new Node[]{
                                        new Node("-1"),
                                        new Node("/", new Node[]{
                                                new Node("+", new Node[]{
                                                        new Node("*", new Node[]{
                                                                new Node("-1"),
                                                                powers[1].clone()
                                                        }),
                                                        new Node("^", new Node[]{
                                                                new Node("+", new Node[]{
                                                                        new Node("^", new Node[]{
                                                                                powers[1].clone(),
                                                                                new Node("2")
                                                                        }),
                                                                        new Node("*", new Node[]{
                                                                                new Node("-4"),
                                                                                powers[2].clone(),
                                                                                powers[0].clone()
                                                                        })
                                                                }),
                                                                new Node("/", new Node[]{
                                                                        new Node("1"),
                                                                        new Node("2")
                                                                })
                                                        })
                                                }),
                                                new Node("*", new Node[]{
                                                        new Node("2"),
                                                        powers[2].clone()
                                                })
                                        })
                                })
                        }),
                        new Node("+", new Node[]{
                                new Node("x"),
                                new Node("*", new Node[]{
                                        new Node("-1"),
                                        new Node("/", new Node[]{
                                                new Node("+", new Node[]{
                                                        new Node("*", new Node[]{
                                                                new Node("-1"),
                                                                powers[1].clone()
                                                        }),
                                                        new Node("*", new Node[]{
                                                                new Node("-1"),
                                                                new Node("^", new Node[]{
                                                                        new Node("+", new Node[]{
                                                                                new Node("^", new Node[]{
                                                                                        powers[1].clone(),
                                                                                        new Node("2")
                                                                                }),
                                                                                new Node("*", new Node[]{
                                                                                        new Node("-4"),
                                                                                        powers[2].clone(),
                                                                                        powers[0].clone()
                                                                                })
                                                                        }),
                                                                        new Node("/", new Node[]{
                                                                                new Node("1"),
                                                                                new Node("2")
                                                                        })
                                                                })
                                                        })
                                                }),
                                                new Node("*", new Node[]{
                                                        new Node("2"),
                                                        powers[2].clone()
                                                })
                                        })
                                })
                        })
                });
                n.temp = -20;
                eq.nodes.set(0, n);
                eq.nodes.set(1, new Node("0"));
                break;
            }
        }

        return false;
    }

    public static boolean Multis(Node x, Node y, Node eq, boolean toLeft) {

        if (x.value.equals("*") && y.value.equals("*")) {
            for (int i = 0; i < x.nodes.size(); i++) {
                Node a = x.nodes.get(i);
                for (int j = 0; j < y.nodes.size(); j++) {
                    Node b = y.nodes.get(j);
                    if (a.equals(b)) {
                        eq.nodes.set(0, new Node("/", new Node[]{
                                eq.nodes.get(0),
                                a.clone()
                        }));
                        eq.nodes.set(1, new Node("/", new Node[]{
                                eq.nodes.get(1),
                                a.clone()
                        }));
                        return true;
                    }
                }
            }
        } else if (x.value.equals("*")) {
            for (int i = 0; i < x.nodes.size(); i++) {
                Node a = x.nodes.get(i);
                if (a.equals(y)) {
                    eq.nodes.set(0, new Node("/", new Node[]{
                            eq.nodes.get(0),
                            a.clone()
                    }));
                    eq.nodes.set(1, new Node("/", new Node[]{
                            eq.nodes.get(1),
                            a.clone()
                    }));
                    return true;
                }
            }
        } else if (y.value.equals("*")) {
            for (int i = 0; i < y.nodes.size(); i++) {
                Node a = y.nodes.get(i);
                if (a.equals(x)) {
                    eq.nodes.set(0, new Node("/", new Node[]{
                            eq.nodes.get(0),
                            a.clone()
                    }));
                    eq.nodes.set(1, new Node("/", new Node[]{
                            eq.nodes.get(1),
                            a.clone()
                    }));
                    return true;
                }
            }
        }


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
                x.clone(x.nodes.get(0));
                y.clone(y.nodes.get(0));

                return true;
            }
        }

        if (x.value.equals("^") && x.nodes.get(1).targets == 0) {

            Node temp;
            boolean set = false;

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
                Node ex = x.nodes.get(1);
                Node guts = new Node("/", new Node[]{
                        new Node("1"),
                        ex
                });
                guts.changed = true;

                temp = new Node("^", new Node[]{
                        y,
                        guts
                });


                if (Bools.isNum(ex.value)) {
                    float val = Float.parseFloat(ex.value);
                    if (val % 2 == 0) {
                        set = true;
                    }
                }

                x.nodes.set(1, new Node("1"));
            }

            eq.nodes.set(toLeft ? 1 : 0, temp);

            if (set) {
                eq.clone(new Node(",", new Node[]{
                        new Node("=", new Node[]{
                                eq.nodes.get(toLeft ? 0 : 1),
                                eq.nodes.get(toLeft ? 1 : 0)
                        }),
                        new Node("=", new Node[]{
                                eq.nodes.get(toLeft ? 0 : 1).clone(),
                                new Node("*", new Node[]{
                                        new Node("-1"),
                                        eq.nodes.get(toLeft ? 1 : 0).clone()
                                })
                        })
                }));
            }

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
                x.clone(x.nodes.get(0));
                y.clone(y.nodes.get(0));

                return true;
            }

            if (y.targets > 0) {
                // cross multiply
                Node temp = new Node("*", new Node[]{
                        x.nodes.get(0),
                        y.nodes.get(1)
                }, true);

                y.clone(new Node("*", new Node[]{
                        y.nodes.get(0),
                        x.nodes.get(1)
                }, true));

                x.clone(temp);

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
            x.nodes.set(1, new Node("1", true));

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
        if (eq.valEquals(",")) {
            for (int i = 0; i < eq.nodes.size(); ++i) {
                if (!Step(eq.nodes.get(i), target, e)) return false;
            }
            return true;
        }

        Node last = eq.clone();

        Parser.MarkUp(eq, target);
        PreSolve(eq, target);
        if (!Simplify.Step(eq)) return false;
        Solve(eq, target);

        boolean eqs = eq.equals(last);

        if (!e && eqs) {
            return Step(eq, target, true);
        }
        return eqs;
    }

    public static boolean Step(Node eq, String target) {
        return Step(eq, target, false);
    }

    public static void Run(String eq, String target) throws Exception {
        System.out.println("\nRUN...");
        Node n = Parser.CreateNode(eq, target);
        System.out.println(Parser.ReadNode(n));
        int i;

        for (i = 0; i < 100; i++) {
            if (Step(n, target)) break;
            Parser.MarkUp(n);
            System.out.println(Parser.ReadNode(n));
        }
        Parser.MarkUp(n);
        System.out.println(Parser.ReadNode(n));
        System.out.println("Done... " + i);
    }
}
