package com.mathenator.engine;

import sun.tools.tree.NewArrayExpression;

public class Simplify {
    public static boolean Simplify(Node node) {
        node.changed = false;
        node.message = null;

        if (node.value.equalsIgnoreCase("pi")) {
            node.value = Math.PI + "";
            node.nodes.clear();
        }

        for (int i = 0; i < node.nodes.size(); i++) {
            node.nodes.get(i).changed = false;
            if (Simplify(node.nodes.get(i))) return true;
        }


        if (node.nodes.size() == 0) {
            if (node.value.equals("+")) {
                node.value = "0";

                node.changed = true;
                return true;
            } else if (node.value.equals("*")) {
                node.value = "1";

                node.changed = true;
                return true;
            }
            return false;
        }
        if (!Bools.isFn(node.value) && node.nodes.size() == 1) {
            Node n = node.nodes.get(0);
            node.nodes.clear();
            node.value = n.value;
            node.nodes.addAll(n.nodes);
            return false;
        }
        if (node.parent != null && node.value.equals(node.parent.value)) {
            if (!(node.value.equals("^") || node.value.equals("/"))) {
                node.parent.nodes.remove(node);
                node.parent.nodes.addAll(node.nodes);
                node.parent.simple = false;
                node = node.parent;
            }
        }

        if (Add(node)) return true;
        if (Multiply(node)) return true;
        if (Divide(node)) return true;
        if (Power(node)) return true;
        if (Function(node)) return true;
        if (Derive(node)) return true;

        return false;
    }

    public static boolean Function(Node node) {
        if (Bools.isFn(node.value)) {
            if (Bools.isNum(node.nodes.get(0).value)) {
                double val = Double.parseDouble(node.nodes.get(0).value), res;

                if (node.value.equalsIgnoreCase("sin")) {
                    res = Math.sin(val);
                }

                node.value = val + "";
                node.nodes.clear();

                return true;
            }
        }

        return false;
    }

    public static boolean Derive(Node node) {

        if (node.value.equals("D")) {
            Node a = node.nodes.get(0);
            Node target = new Node("x");
            if (a.value.equals(",")) {
                target = a.nodes.get(1);
                a = a.nodes.get(0);
            }

            if (a.value.equals("D")) {
                boolean run = false;
                if (a.nodes.get(0).value.equals(",")) {
                    if (a.nodes.get(0).nodes.get(1).equals(target)) run = true;
                } else if (target.equals(new Node("x"))) run = true;

                if (run) {
                    node.value = a.value;
                    node.nodes = a.nodes;
                }
            }

            if (a.value.equals("+")) {
                Node res = new Node("+");
                for (int i = 0; i < a.nodes.size(); i++) {
                    Node c = a.nodes.get(i);

                    if (!c.contains(target)) {
                        a.nodes.remove(i);
                        return true;
                    }

                    res.nodes.add(new Node("D", new Node[] {
                            new Node(",", new Node[] {
                                    c,
                                    target
                            })
                    }));
                }

                node.value = res.value;
                node.nodes = res.nodes;
                node.changed = true;

                return true;
            }

            if (a.value.equals("*")) {
                Node f = a.nodes.get(0),
                        b = a.nodes.get(1);

                Node temp = new Node("+", new Node[] {
                        new Node("*", new Node[] {
                                new Node("D", new Node[] {
                                        new Node(",", new Node[] {
                                                f.clone()
                                        }),
                                        target
                                }),
                                b
                        }),
                        new Node("*", new Node[] {
                                new Node("D", new Node[] {
                                        new Node(",", new Node[] {
                                                b.clone()
                                        }),
                                        target
                                }),
                                f
                        })
                });

                temp.changed = true;
                a.nodes.set(0, temp);
                a.nodes.remove(1);

                return true;
            }

            if (a.equals(target)) {
                node.value = "1";
                node.nodes.clear();

                return true;
            }

            if (a.value.equals("^")) {
                Node b = a.nodes.get(0),
                        e = a.nodes.get(1);

                Node temp = new Node("*", new Node[] {
                        e.clone(),
                        new Node("D", new Node[] {
                                new Node(",", new Node[] {
                                        b.clone(),
                                        target
                                })
                        }),
                        new Node("^", new Node[] {
                                b,
                                new Node("+", new Node[] {
                                        e,
                                        new Node("-1")
                                })
                        })
                });

                node.value = temp.value;
                node.nodes = temp.nodes;
                node.changed = true;

                return true;
            }

            if (a.value.equals("/")) {
                Node n = a.nodes.get(0),
                        d = a.nodes.get(1);

                Node temp = new Node("/", new Node[] {
                        new Node("+", new Node[] {
                                new Node("*", new Node[] {
                                        d.clone(),
                                        new Node("D", new Node[] {
                                                new Node(",", new Node[] {
                                                        n.clone(),
                                                        target
                                                })
                                        })
                                }),
                                new Node("*", new Node[] {
                                        new Node("-1"),
                                        n,
                                        new Node("D", new Node[] {
                                                new Node(",", new Node[] {
                                                        d.clone(),
                                                        target
                                                })
                                        })
                                })
                        }),
                        new Node("^", new Node[] {
                                d,
                                new Node("2")
                        })
                });

                a.value = temp.value;
                a.nodes = temp.nodes;
                a.changed = true;

                return true;
            }
        }

        return false;
    }

    public static boolean Power(Node node) {
        if (node.value.equals("^")) {
            Node b = node.nodes.get(0),
                    e = node.nodes.get(1);

            if (Bools.isNum(e.value)) {
                float val = Float.parseFloat(e.value);
                if (val == 1) {
                    node.value = b.value;
                    node.nodes = b.nodes;

                    node.changed = true;
                    node.message = "x^1 = x";
                    return true;
                }

                if (val == 0) {
                    node.value = "1";
                    node.nodes.clear();

                    node.changed = true;
                    node.message = "x^0 = 1";
                    return true;
                }
            }

            if (b.value.charAt(0) == '-') {
                // handle issue or do imaginary #s
            }

            if (Bools.isNum(b.value) && Bools.isNum(e.value)) {
                double val = Math.pow(Float.parseFloat(b.value), Float.parseFloat(e.value));
                node.value = val + "";
                node.nodes.clear();

                node.changed = true;
                return true;
            } else if (Bools.isNum(b.value) && e.equals(new Node("/", new Node[]{
                    new Node("1"),
                    new Node("2")
            }))) {
                double val = Math.sqrt(Float.parseFloat(b.value));
                if (val == Math.floor(val)) {
                    node.value = val + "";
                    node.nodes.clear();

                    node.changed = true;
                    return true;
                }
            } else if (b.value.equals("^")) {
                b.nodes.set(1, new Node("*", new Node[]{
                        b.nodes.get(1),
                        e
                }));
                node.value = b.value;
                node.nodes = b.nodes;

                node.changed = true;
                return true;
            } else if (b.value.equals("/")) {
//                Node temp = new Node("/", new Node[]{
//                        new Node("^", new Node[]{
//                                b.nodes.get(0),
//                                e.clone()
//                        }),
//                        new Node("^", new Node[]{
//                                b.nodes.get(1),
//                                e.clone()
//                        })
//                });
//                node.value = temp.value;
//                node.nodes = temp.nodes;
//
//                node.changed = true;
//                return true;
            } else if (b.value.equals("*")) {
                for (int i = 0; i < b.nodes.size(); i++) {
                    b.nodes.set(i, new Node("^", new Node[]{
                            b.nodes.get(i),
                            e.clone()
                    }));
                }
                node.value = "*";
                node.nodes = b.nodes;

                node.changed = true;
                return true;
            }
        }

        return false;
    }

    public static boolean Multiply(Node node) {
        if (node.value.equals("*")) {
            for (int i = 0; i < node.nodes.size(); ++i) {
                Node a = node.nodes.get(i);
                for (int j = 0; j < node.nodes.size(); ++j) {
                    if (i == j) continue;
                    Node b = node.nodes.get(j);

                    if (Bools.isNum(a.value)) {
                        float val = Float.parseFloat(a.value);
                        if (val == 1) {
                            node.nodes.remove(i);

                            node.changed = true;
                            node.message = "1*x = x";
                            return true;
                        }

                        if (val == 0) {
                            node.value = "0";
                            node.nodes.clear();

                            node.changed = true;
                            node.message = "0*x = 0";
                            return true;
                        }
                    }

                    if (Bools.isNum(a.value) && Bools.isNum(b.value)) {
                        float res = Float.parseFloat(a.value) * Float.parseFloat(b.value);
                        node.nodes.set(i, new Node(res + ""));

                        node.nodes.get(i).changed = true;
                        node.nodes.get(i).message = "multiplied " + a.value
                                + "and " + b.value + " and got " + res + "\n"
                                + a.value + "*" + b.value + "=" + res;

                        node.nodes.remove(j);
                        return true;
                    }

                    if (a.value.equals("+")) {
//                        if (Bools.isNum(b.value)) {
                        for (int n = 0; n < a.nodes.size(); n++) {
                            a.nodes.set(n, new Node("*", new Node[]{
                                    b.clone(),
                                    a.nodes.get(n)
                            }));

                        }
                        a.changed = true;
                        a.message = "distributed x*(y+z) = x*y + x*z";

                        node.nodes.remove(j);
                        return true;
//                        }
                    }

                    if (a.equals(b)) {
                        node.nodes.set(i, new Node("^", new Node[]{
                                a,
                                new Node("2")
                        }));

                        node.nodes.get(i).changed = true;
                        node.nodes.get(i).message = "x*x = x^2";
                        node.nodes.remove(j);
                        return true;
                    }

                    if (a.value.equals("^")) {
                        if (b.value.equals("^")) {
                            if (a.nodes.get(0).equals(b.nodes.get(0))) {
                                Node temp = new Node("+", new Node[]{
                                        a.nodes.get(1),
                                        b.nodes.get(1)
                                });

                                a.nodes.set(1, temp);
                                node.nodes.remove(j);

                                a.changed = true;
                                a.message = "x^n*x^w = x^(n+w)";
                                return true;
                            }
                        } else {
                            if (a.nodes.get(0).equals(b)) {
                                a.nodes.set(1, new Node("+", new Node[]{
                                        a.nodes.get(1),
                                        new Node("1")
                                }));
                                node.nodes.remove(j);

                                a.changed = true;
                                a.message = "x^n*x = x^(n+1)";
                                return true;
                            }
                        }
                    }

                    if (a.value.equals("/")) {
                        if (b.value.equals("/")) {
                            node.nodes.set(i, new Node("/", new Node[]{
                                    new Node("*", new Node[]{
                                            a.nodes.get(0),
                                            b.nodes.get(0)
                                    }),
                                    new Node("*", new Node[]{
                                            a.nodes.get(1),
                                            b.nodes.get(1)
                                    })
                            }));

                            node.nodes.get(i).changed = true;
                            node.nodes.get(i).message = "x/y*z/w = (x*z)/(y*w)";
                            node.nodes.remove(j);
                            return true;
                        }

                        Node temp = new Node("*", new Node[]{
                                a.nodes.get(0),
                                b
                        });

                        node.nodes.set(i, new Node("/", new Node[]{
                                temp,
                                a.nodes.get(1)
                        }));

                        node.nodes.remove(j);

                        temp.changed = true;
                        temp.message = "x/y*z = (x*z)/y";
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean Divide(Node node) {
        if (node.value.equals("/")) {

            Node n = node.nodes.get(0),
                    d = node.nodes.get(1);

            if (Bools.isNum(n.value) && Float.parseFloat(n.value) == 0) {
                node.value = "0";
                node.nodes.clear();

                node.changed = true;
                node.message = "0/x = 0";
                return true;
            }

            if (Bools.isNum(d.value) && Float.parseFloat(d.value) == 1) {
                node.value = node.nodes.get(0).value;
                node.nodes = node.nodes.get(0).nodes;
                return true;
            }

            if (n.equals(d)) {
                node.value = "1";
                node.nodes.clear();

                node.changed = true;
                node.message = "x/1 = x";
                return true;
            }

            if (Bools.isNum(n.value) && Bools.isNum(d.value)) {
                float a = Float.parseFloat(n.value),
                        b = Float.parseFloat(d.value);

                float res = a / b;

                if (Math.floor(res) == res) {
                    node.value = res + "";
                    node.nodes.clear();

                    node.changed = true;

                    return true;
                }

                int[] Simple = nMath.SimplifyFraction(a, b);
                if (Simple != null && Simple[0] != a) {
                    node.nodes.set(0, new Node(Simple[0] + ""));
                    node.nodes.set(1, new Node(Simple[1] + ""));

                    node.changed = true;
                    return true;
                }
            }

            if (n.value.equals("/") && d.value.equals("/")) {
                node.nodes.set(0, new Node("*", new Node[]{
                        n.nodes.get(0),
                        d.nodes.get(1)
                }));
                node.nodes.set(1, new Node("*", new Node[]{
                        n.nodes.get(1),
                        d.nodes.get(0)
                }));

                node.changed = true;
                return true;
            }

            if (n.value.equals("/")) {
                node.nodes.set(0, n.nodes.get(0));
                node.nodes.set(1, new Node("*", new Node[]{
                        n.nodes.get(1),
                        d
                }));

                node.changed = true;
                return true;
            }

            if (d.value.equals("/")) {
                node.nodes.set(0, new Node("*", new Node[]{
                        n,
                        d.nodes.get(1)
                }));
                node.nodes.set(1, d.nodes.get(0));

                node.changed = true;
                return true;
            }

            if (n.value.equals("*") && d.value.equals("*")) {
                for (int i = 0; i < n.nodes.size(); i++) {
                    Node a = n.nodes.get(i);
                    for (int j = 0; j < d.nodes.size(); j++) {
                        Node b = d.nodes.get(j);

                        if (a.equals(b)) {
                            n.nodes.remove(i);
                            d.nodes.remove(j);

                            node.changed = true;
                            node.message = "x/x = 1, x*y/x = y";
                            return true;
                        }

                        if (a.value.equals("^") && b.value.equals("^")) {
                            if (a.nodes.get(0).equals(b.nodes.get(0))) {
                                a.nodes.set(1, new Node("+", new Node[]{
                                        a.nodes.get(1),
                                        new Node("*", new Node[]{
                                                new Node("-1"),
                                                b.nodes.get(1)
                                        })
                                }));

                                a.nodes.get(1).changed = true;
                                d.nodes.remove(j);
                                return true;
                            }
                        } else if (a.value.equals("^")) {
                            if (a.nodes.get(0).equals(b)) {
                                a.nodes.set(1, new Node("+", new Node[]{
                                        a.nodes.get(1),
                                        new Node("-1")
                                }));

                                a.nodes.get(1).changed = true;
                                d.nodes.remove(j);
                                return true;
                            }
                        } else if (b.value.equals("^")) {
                            if (b.nodes.get(0).equals(a)) {
                                b.nodes.set(1, new Node("+", new Node[]{
                                        b.nodes.get(1),
                                        new Node("-1")
                                }));

                                b.nodes.get(1).changed = true;
                                n.nodes.remove(i);
                                return true;
                            }
                        }
                    }
                }
            } else if (n.value.equals("*")) {
                for (int i = 0; i < n.nodes.size(); i++) {
                    Node a = n.nodes.get(i);
                    if (a.equals(d)) {
                        n.nodes.remove(i);
                        node.nodes.remove(1);

                        node.changed = true;
                        return true;
                    }

                    if (a.value.equals("^") && d.value.equals("^")) {
                        if (a.nodes.get(0).equals(d.nodes.get(0))) {
                            a.nodes.set(1, new Node("+", new Node[]{
                                    a.nodes.get(1),
                                    new Node("*", new Node[]{
                                            new Node("-1"),
                                            d.nodes.get(1)
                                    })
                            }));

                            a.nodes.get(1).changed = true;
                            node.nodes.remove(1);
                            return true;
                        }
                    } else if (a.value.equals("^")) {
                        if (a.nodes.get(0).equals(d)) {
                            a.nodes.set(1, new Node("+", new Node[]{
                                    a.nodes.get(1),
                                    new Node("-1")
                            }));

                            a.nodes.get(1).changed = true;
                            node.nodes.remove(1);
                            return true;
                        }
                    } else if (d.value.equals("^")) {
                        if (d.nodes.get(0).equals(a)) {
                            d.nodes.set(1, new Node("+", new Node[]{
                                    d.nodes.get(1),
                                    new Node("-1")
                            }));

                            d.nodes.get(1).changed = true;
                            n.nodes.remove(i);
                            return true;
                        }
                    }
                }
            } else if (n.value.equals("+") && d.targets == 0) {
                Node targets = new Node("+");
                Node norms = new Node("+");

                for (int i = 0; i < n.nodes.size(); i++) {
                    Node c = n.nodes.get(i);
                    if (c.targets > 0) {
                        targets.nodes.add(c);
                    } else {
                        norms.nodes.add(c);
                    }
                }

                if (targets.nodes.size() > 0 && norms.nodes.size() > 0) {
                    node.value = "+";
                    node.nodes.clear();
                    node.nodes.add(new Node("/", new Node[]{
                            targets,
                            d
                    }));
                    node.nodes.add(new Node("/", new Node[]{
                            norms,
                            d
                    }));

                    node.changed = true;
                    return true;
                }
            } else if (n.value.equals("^") && d.value.equals("^")) {
                if (n.nodes.get(0).equals(d.nodes.get(0))) {
                    Node temp = new Node("+", new Node[]{
                            n.nodes.get(1).clone(),
                            new Node("*", new Node[]{
                                    new Node("-1"),
                                    d.nodes.get(1)
                            })
                    });

                    temp.changed = true;
                    n.nodes.set(1, temp);
                    node.nodes.remove(1);

                    return true;
                }
            } else if (n.value.equals("^")) {
                if (n.nodes.get(0).equals(d)) {
                    Node temp = new Node("+", new Node[]{
                            n.nodes.get(1).clone(),
                            new Node("-1")
                    });
                    temp.changed = true;
                    n.nodes.set(1, temp);
                    node.nodes.remove(1);

                    return true;
                }
            } else if (d.value.equals("^")) {
                if (d.nodes.get(0).equals(n)) {
                    Node guts = new Node("+", new Node[]{
                            new Node("1"),
                            new Node("*", new Node[]{
                                    new Node("-1"),
                                    d.nodes.get(1).clone()
                            })
                    });

                    Node temp = new Node("^", new Node[]{
                            n.clone(),
                            guts
                    });

                    guts.changed = true;
                    n.value = temp.value;
                    n.nodes = temp.nodes;
                    node.nodes.remove(1);

                    return true;
                }
            }
        }

        return false;
    }

    public static boolean Add(Node node) {
        if (node.value.equals("+")) {
            float sum = 0;
            for (int i = 0; i < node.nodes.size(); ++i) {
                Node a = node.nodes.get(i);

                if (Bools.isNum(a.value) && Float.parseFloat(a.value) == 0) {
                    node.nodes.remove(i);
                    return true;
                }

                for (int j = 0; j < node.nodes.size(); ++j) {
                    if (i == j) continue;

                    Node b = node.nodes.get(j);

                    if (Bools.isNum(a.value) && Bools.isNum(b.value)) {
                        sum = Float.parseFloat(a.value) + Float.parseFloat(b.value);

                        Node temp = new Node(sum + "");
                        temp.changed = true;
                        temp.message = "added " + a.value
                                + " and " + b.value
                                + " and got " + sum + '\n'
                                + '(' + a.value + '+' + b.value + '=' + sum + ')';

                        if (i > j) {
                            node.nodes.remove(i);
                            node.nodes.set(j, temp);
                        } else {
                            node.nodes.remove(j);
                            node.nodes.set(i, temp);
                        }

                        return true;

                    } else if (a.equals(b)) {
                        Node temp = new Node("*", new Node[]{
                                new Node("2"),
                                a
                        });

                        temp.changed = true;
                        temp.message = "x+x = 2*x";

                        if (i > j) {
                            node.nodes.remove(i);
                            node.nodes.set(j, temp);
                        } else {
                            node.nodes.remove(j);
                            node.nodes.set(i, temp);
                        }

                        return true;
                    } else if (a.value.equals("*")) {
                        if (b.value.equals("*")) {
                            if (a.nodes.size() == 2 && b.nodes.size() == 2) {
                                for (int n = 0; n <= 1; n++) {
                                    for (int g = 0; g <= 1; g++) {
                                        if (a.nodes.get(n).equals(b.nodes.get(g))) {
                                            int n1 = n == 1 ? 0 : 1,
                                                    g1 = g == 1 ? 0 : 1;

                                            a.nodes.set(n1, new Node("+", new Node[]{
                                                    a.nodes.get(n1),
                                                    b.nodes.get(g1)
                                            }));
                                            node.nodes.remove(j);

                                            a.changed = true;
                                            a.message = "x*y+z*y = y*(x+z)";
                                            return true;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (a.nodes.size() == 2) {
                                if (a.nodes.get(0).equals(b)) {
                                    if (Bools.isNum(a.nodes.get(1).value)) {
                                        a.nodes.get(1).value = (Float.parseFloat(a.nodes.get(1).value) + 1) + "";
                                        node.nodes.remove(j);

                                        a.changed = true;
                                        a.message = "3*x+x = 4*x";
                                        return true;
                                    }
                                } else if (a.nodes.get(1).equals(b)) {
                                    if (Bools.isNum(a.nodes.get(0).value)) {
                                        a.nodes.get(0).value = (Float.parseFloat(a.nodes.get(0).value) + 1) + "";
                                        node.nodes.remove(j);

                                        a.changed = true;
                                        a.message = "3*x+x = 4*x";
                                        return true;
                                    }
                                }
                            }
                        }
                    } else if (a.value.equals("/") && b.value.equals("/")) {
                        if ((a.targets > 0 && b.targets > 0) || (a.targets == 0 && b.targets == 0)) {
                            if (a.nodes.get(1).equals(b.nodes.get(1))) {
                                node.nodes.set(i, new Node("/", new Node[]{
                                        new Node("+", new Node[]{
                                                a.nodes.get(0),
                                                b.nodes.get(0)
                                        }),
                                        a.nodes.get(1)
                                }));

                                node.nodes.get(i).changed = true;
                                node.nodes.get(i).message = "adding fractions with common denominator\n" +
                                        "x/y+z/y = (x+z)/y";
                                node.nodes.remove(j);
                                return true;
                            }

                            node.nodes.set(i, new Node("/", new Node[]{
                                    new Node("+", new Node[]{
                                            new Node("*", new Node[]{
                                                    b.nodes.get(1),
                                                    a.nodes.get(0)
                                            }),
                                            new Node("*", new Node[]{
                                                    a.nodes.get(1),
                                                    b.nodes.get(0)
                                            })
                                    }),
                                    new Node("*", new Node[]{
                                            a.nodes.get(1),
                                            b.nodes.get(1)
                                    })
                            }));


                            node.nodes.get(i).changed = true;
                            node.nodes.get(i).message = "adding fractions with different denominators \n" +
                                    "x/y+z/w = (x*w+z*y)/(w*y)";
                            node.nodes.remove(j);
                            return true;
                        }

                    } else if (a.value.equals("/") || b.value.equals("/")) {
                        Node div, base;
                        if (a.value.equals("/")) {
                            div = a;
                            base = b;
                        } else {
                            div = b;
                            base = a;
                        }

                        if (div.targets > 0 && base.targets > 0 || div.targets == 0 && base.targets == 0) {

                            Node temp = new Node("*", new Node[]{
                                    base,
                                    div.nodes.get(1)
                            });

                            node.nodes.set(i, new Node("/", new Node[]{
                                    new Node("+", new Node[]{
                                            temp,
                                            div.nodes.get(0)
                                    }),
                                    div.nodes.get(1)
                            }));


                            temp.changed = true;
                            temp.message = "adding fraction to non-fraction\n" +
                                    "x/y+z = (x+z*y)/y";

                            node.nodes.remove(j);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean Step(Node node) {
        Node last = node.clone();
        Simplify(node);
        return node.equals(last);
    }

    public static void Run(String eq) throws Exception {
        Node n = Parser.CreateNode(eq);
        Parser.MarkUp(n);
        System.out.println(Parser.ReadNode(n));
        int t = 0;
        for (int i = 0; i < 1000; i++) {
            Parser.MarkUp(n);
            System.out.println(Parser.ReadNode(n));
            if (Step(n)) ++t;
            else t = 0;
            if (t >= 5) break;
        }
        Parser.MarkUp(n);
        System.out.println(Parser.ReadNode(n));
    }
}
