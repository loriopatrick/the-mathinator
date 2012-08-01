package com.mathinator.engine;

import java.util.ArrayList;

public class Simplify {
    public static boolean Simplify(Node node, boolean expand) {
        node.changed = false;
        node.message = null;

        boolean done = false;

        if (node.valEquals("/") && node.nodes.get(0).valEquals("+")) {
            if (Divide(node, false))
                return true;
        }

        for (int i = 0; i < node.nodes.size(); i++) {
            node.nodes.get(i).changed = false;
            if (!done) {
                if (Simplify(node.nodes.get(i), expand)) done = true;
            }
        }

        if (done) return true;


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
            node.clone(n);
            return false;
        }
        if (node.parent != null && node.value.equals(node.parent.value)) {
            if (!(node.value.equals("^") || node.value.equals("/"))) {
                node.parent.nodes.remove(node);
                node.parent.nodes.addAll(node.nodes);
                node = node.parent;
            }
        }

        if (Factor(node)) return true;
        if (Add(node, expand)) return true;
        if (Multiply(node, expand)) return true;
        if (Divide(node, expand)) return true;
        if (Power(node)) return true;
        if (Function(node)) return true;
        if (Derive(node)) return true;

        return false;
    }

    public static boolean Factor(Node node) {
        if (node.value.equals("=")) {
            Node x = node.nodes.get(0), y = node.nodes.get(1);

            if (x.valEquals("1") || x.valEquals("0") || y.valEquals("1") || y.valEquals("0")) return false;

            boolean useTarget = true;
            Node target = null;

            if (x.targets == 1 && x.nodes.size() == 0) {
                useTarget = false;
                target = x;
            } else if (y.targets == 1 && y.nodes.size() == 0) {
                useTarget = false;
                target = y;
            }

            ArrayList<Node> commons = nMath.Commons(x);
            ArrayList<Node> yCommons = nMath.Commons(y);

            nMath.EliminateNon(commons, yCommons);

            if (commons.size() > 0) {
                Node div = new Node("*");
                for (Node n : commons) {
                    if (!useTarget && n.equals(target)) {
                        continue;
                    }
                    div.nodes.add(n);
                }

                if (div.nodes.size() == 0) return false;

                node.nodes.set(0, new Node("/", new Node[]{
                        node.nodes.get(0),
                        div
                }));

                node.nodes.set(1, new Node("/", new Node[]{
                        node.nodes.get(1),
                        div.clone()
                }));

                return true;
            }
        }

        return false;
    }

    public static boolean Function(Node node) {
        if (Bools.isFn(node.value)) {
            Node in = node.nodes.get(0);
            Node res = null;

            if (in.value.equals("/")) {
                Node n = in.nodes.get(1);
                if (in.nodes.get(0).find(new Node("pi")) <= 1 && Bools.isNum(n.value)) {

                    int mode = 0;
                    if (in.nodes.get(0).valEquals("*")) {
                        if (in.nodes.get(0).nodes.size() != 2) {
                            mode = -1;
                        } else {
                            Node a = in.nodes.get(0).nodes.get(0);
                            if (a.valEquals("pi")) a = in.nodes.get(0).nodes.get(1);
                            if (Bools.isNum(a.value)) {
                                Float val = Float.parseFloat(a.value);
                                if (Math.floor(val) == val) {
                                    mode = (int) Math.floor(val);
                                }
                            }
                        }
                    } else if (in.nodes.get(0).valEquals("pi")) {
                        mode = 0;
                    }

                    if (mode != -1) {

                        if (n.valEquals("3")) {
                            if (node.valEquals("sin")) {
                                res = new Node("/", new Node[]{
                                        new Node("*", new Node[]{
                                                (mode == 4 || mode == 5) ? new Node("-1") : new Node("1"),
                                                new Node("^", new Node[]{
                                                        new Node("3"),
                                                        new Node("/", new Node[]{
                                                                new Node("1"),
                                                                new Node("2")
                                                        })
                                                })
                                        }),
                                        new Node("2")
                                });
                            } else if (node.valEquals("cos")) {
                                res = new Node("/", new Node[]{
                                        (mode == 2 || mode == 4) ? new Node("-1") : new Node("1"),
                                        new Node("2")
                                });
                            }

                        } else if (n.valEquals("4")) {
                            if (node.valEquals("sin")) {
                                res = new Node("/", new Node[]{
                                        new Node("*", new Node[]{
                                                (mode == 5 || mode == 7) ? new Node("-1") : new Node("1"),
                                                new Node("^", new Node[]{
                                                        new Node("2"),
                                                        new Node("/", new Node[]{
                                                                new Node("1"),
                                                                new Node("2")
                                                        })
                                                })
                                        }),
                                        new Node("2")
                                });
                            } else if (node.valEquals("cos")) {
                                res = new Node("/", new Node[]{
                                        new Node("*", new Node[]{
                                                (mode == 5 || mode == 3) ? new Node("-1") : new Node("1"),
                                                new Node("^", new Node[]{
                                                        new Node("2"),
                                                        new Node("/", new Node[]{
                                                                new Node("1"),
                                                                new Node("2")
                                                        })
                                                })
                                        }),
                                        new Node("2")
                                });
                            }
                        } else if (n.valEquals("6")) {
                            if (node.valEquals("sin")) {
                                res = new Node("/", new Node[]{
                                        (mode == 11 || mode == 7) ? new Node("-1") : new Node("1"),
                                        new Node("2")
                                });
                            } else if (node.valEquals("cos")) {
                                res = new Node("/", new Node[]{
                                        new Node("*", new Node[]{
                                                (mode == 5 || mode == 7) ? new Node("-1") : new Node("1"),
                                                new Node("^", new Node[]{
                                                        new Node("3"),
                                                        new Node("/", new Node[]{
                                                                new Node("1"),
                                                                new Node("2")
                                                        })
                                                })
                                        }),
                                        new Node("2")
                                });
                            }
                        }


                        if (res == null && Bools.isNum(in.nodes.get(0).value) && Bools.isNum(in.nodes.get(1).value)) {
                            float val = Float.parseFloat(in.nodes.get(0).value) / Float.parseFloat(in.nodes.get(1).value);
                            double resVal = Double.POSITIVE_INFINITY;
                            if (node.valEquals("sin")) {
                                resVal = Math.sin(val);
                            } else if (node.valEquals("cos")) {
                                resVal = Math.cos(val);
                            } else if (node.valEquals("tan")) {
                                resVal = Math.tan(val);
                            }

                            if (resVal != Double.POSITIVE_INFINITY && Math.floor(resVal) == resVal) {
                                res = new Node(resVal + "");
                            }
                        }
                    }
                }

                if (res != null) {
                    node.clone(res);
                    node.changed = true;
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean Derive(Node node) {

        if (node.valEquals("d")) {
            Node a = node.nodes.get(0);
            Node target = new Node("x");
            if (a.value.equals(",")) {
                target = a.nodes.get(1);
                a = a.nodes.get(0);
            }

            if (a.value.equals("+")) {
                Node res = new Node("+", true);
                for (int i = 0; i < a.nodes.size(); i++) {
                    Node c = a.nodes.get(i);

                    if (c.contains(target)) {
                        res.nodes.add(new Node("d", new Node[]{
                                new Node(",", new Node[]{
                                        c,
                                        target
                                })
                        }));
                    }
                }

                node.clone(res);

                return true;
            }

            if (a.value.equals("*")) {

                Node f = null, b = null;
                int e = 0, g = 1;

                for (int i = 0; i < a.nodes.size(); i++) {
                    if (!a.nodes.get(i).contains(target)) continue;
                    if (f == null) {
                        f = a.nodes.get(i);
                        e = i;
                    } else if (b == null) {
                        b = a.nodes.get(i);
                        g = i;
                    } else {
                        break;
                    }
                }

                if (f != null && b != null) {
                    Node temp = new Node("+", new Node[]{
                            new Node("*", new Node[]{
                                    new Node("d", new Node[]{
                                            new Node(",", new Node[]{
                                                    f.clone()
                                            }),
                                            target
                                    }),
                                    b
                            }),
                            new Node("*", new Node[]{
                                    new Node("d", new Node[]{
                                            new Node(",", new Node[]{
                                                    b.clone()
                                            }),
                                            target
                                    }),
                                    f
                            })
                    });

                    temp.changed = true;
                    a.nodes.set(e, temp);
                    a.nodes.remove(g);
                } else if (f != null) {
                    a.nodes.set(e, new Node("d", new Node[]{
                            new Node(",", new Node[]{
                                    a.nodes.get(e),
                                    target
                            })
                    }));
                    node.clone(a);
                }

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

                Node temp = new Node("*", new Node[]{
                        e.clone(),
                        new Node("d", new Node[]{
                                new Node(",", new Node[]{
                                        b.clone(),
                                        target
                                })
                        }),
                        new Node("^", new Node[]{
                                b,
                                new Node("+", new Node[]{
                                        e,
                                        new Node("-1")
                                })
                        })
                }, true);

                node.clone(temp);

                return true;
            }

            if (a.value.equals("/")) {
                Node n = a.nodes.get(0),
                        d = a.nodes.get(1);

                if (!d.contains(target)) {
                    a.nodes.set(0, new Node("d", new Node[]{
                            new Node(",", new Node[]{
                                    a.nodes.get(0),
                                    target
                            })
                    }));
                    node.clone(a);
                } else {

                    Node temp = new Node("/", new Node[]{
                            new Node("+", new Node[]{
                                    new Node("*", new Node[]{
                                            d.clone(),
                                            new Node("d", new Node[]{
                                                    new Node(",", new Node[]{
                                                            n.clone(),
                                                            target
                                                    })
                                            })
                                    }),
                                    new Node("*", new Node[]{
                                            new Node("-1"),
                                            n,
                                            new Node("d", new Node[]{
                                                    new Node(",", new Node[]{
                                                            d.clone(),
                                                            target
                                                    })
                                            })
                                    })
                            }),
                            new Node("^", new Node[]{
                                    d,
                                    new Node("2")
                            })
                    }, true);

                    a.clone(temp);
                }
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
                    node.clone(b);

                    node.changed = true;
                    node.message = "x^1 = x";
                    return true;
                }

                if (val == 0) {
                    node.clone(new Node("1", true));
                    node.message = "x^0 = 1";
                    return true;
                }
            }

            if (b.value.charAt(0) == '-') {
                // handle issue or do imaginary #s
            }

            if (Bools.isNum(b.value) && Bools.isNum(e.value)) {
                double val = Math.pow(Float.parseFloat(b.value), Float.parseFloat(e.value));
                node.clone(new Node(val + "", true));

                return true;
            } else if (Bools.isNum(b.value) && e.equals(new Node("/", new Node[]{
                    new Node("1"),
                    new Node("2")
            }))) {
                double val = Math.sqrt(Float.parseFloat(b.value));
                if (val == Math.floor(val)) {
                    node.clone(new Node(val + "", true));

                    return true;
                }
            } else if (b.value.equals("^")) {
                b.nodes.set(1, new Node("*", new Node[]{
                        b.nodes.get(1),
                        e
                }));
                node.clone(b);

                node.changed = true;
                return true;
            } else if (b.value.equals("/")) {
                //TODO: if base == + then do
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

    public static boolean Multiply(Node node, boolean expand) {
        if (node.value.equals("*")) {
            for (int i = 0; i < node.nodes.size(); ++i) {
                Node a = node.nodes.get(i);
                if (a.nodes.size() == 0) {
                    if (a.value.charAt(0) == '-' && !Bools.isNum(a.value)) {
                        a.value = a.value.substring(1);
                        node.nodes.add(new Node("-1"));
                        return false;
                    }
                }
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

                    if (a.value.equals("+") && expand && a.targets > 0) {
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

    public static boolean Divide(Node node, boolean expand) {
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
                node.clone(node.nodes.get(0));
                return true;
            }

            if (Divide(n, d)) return true;

            if (n.value.equals("+")) {
                if (n.targets > 0 &&
                        (d.targets == 0 || (d.targets > 0 && !d.value.equals("+"))) && expand) {
                    Node temp = new Node("+", true);

                    for (int i = 0; i < n.nodes.size(); i++) {
                        Node c = n.nodes.get(i);
                        temp.nodes.add(new Node("/", new Node[]{
                                c,
                                d.clone()
                        }));
                    }
                    node.clone(temp);

                    return true;
                }
            }
        }

        return false;
    }

    public static boolean Divide(Node n, Node d) {

        if (n.equals(d)) {
            n.clone(new Node("1", true));
            d.clone(new Node("1", true));

            return true;
        }

        if (Bools.isNum(n.value) && Bools.isNum(d.value)) {
            float a = Float.parseFloat(n.value),
                    b = Float.parseFloat(d.value);

            float res = a / b;

            if (Math.floor(res) == res) {
                n.clone(new Node(res + "", true));
                d.clone(new Node("1", true));

                return true;
            }

            int[] Simple = nMath.SimplifyFraction(a, b);
            if (Simple != null && Simple[0] != a) {
                // todo: ??
                n.clone(new Node(Simple[0] + "", true));
                d.clone(new Node(Simple[1] + "", true));

                return true;
            }
        }

        if (n.value.equals("/") && d.value.equals("/")) {
            Node nn = new Node("*", new Node[]{
                    n.nodes.get(0),
                    d.nodes.get(1)
            }, true);
            Node dn = new Node("*", new Node[]{
                    n.nodes.get(1),
                    d.nodes.get(0)
            }, true);

            n.clone(nn);
            d.clone(dn);
            return true;
        }

        if (n.value.equals("/")) {
            d.clone(new Node("*", new Node[]{
                    n.nodes.get(1),
                    d.clone()
            }, true));
            n.clone(n.nodes.get(0));

            n.changed = true;
            return true;
        }

        if (d.value.equals("/")) {
            n.clone(new Node("*", new Node[]{
                    n.clone(),
                    d.nodes.get(1)
            }, true));
            d.clone(d.nodes.get(0));

            d.changed = true;
            return true;
        }

        if (n.value.equals("*") && d.value.equals("*")) {
            for (int i = 0; i < n.nodes.size(); i++) {
                Node a = n.nodes.get(i);
                for (int j = 0; j < d.nodes.size(); j++) {
                    Node b = d.nodes.get(j);
                    if (Divide(a, b)) return true;
                }
            }
        } else if (n.value.equals("*")) {
            for (int i = 0; i < n.nodes.size(); i++) {
                Node a = n.nodes.get(i);
                if (Divide(a, d)) return true;
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
                d.clone(new Node("1", true));

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
                d.clone(new Node("1", true));

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
                n.clone(temp);
                d.clone(new Node("1", true));

                return true;
            }
        }

        return false;
    }

    public static boolean Add(Node node, boolean collapse) {
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
                                            if (!Bools.isNum(a.nodes.get(n).value) && collapse) {
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
                                int outsA = 0, outsB = 0;
                                Node v = new Node(""), n = new Node("");
                                ArrayList<Integer> used = new ArrayList<Integer>();
                                for (int k = 0; k < a.nodes.size(); k++) {
                                    Node x = a.nodes.get(k);
                                    boolean in = false;
                                    for (int p = 0; p < b.nodes.size(); p++) {
                                        if (used.contains(p)) continue;
                                        Node y = b.nodes.get(p);
                                        if (x.equals(y)) {
                                            used.add(p);
                                            in = true;
                                            break;
                                        }
                                    }
                                    if (!in) {
                                        ++outsA;
                                        v = x;
                                    }
                                }

                                if (outsA == 1) {
                                    int pos = 0;
                                    for (int p = 0; p < b.nodes.size(); p++) {
                                        if (used.contains(p)) continue;
                                        ++outsB;
                                        pos = p;
                                        n = b.nodes.get(p);
                                    }

                                    if (outsB == 1) {
                                        b.nodes.set(pos, new Node("+", new Node[]{
                                                n,
                                                v
                                        }));
                                        node.nodes.remove(i);
                                        return true;
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
                            if (a.nodes.get(1).equals(b.nodes.get(1)) && collapse) {
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
                                            a.nodes.get(1).clone(),
                                            b.nodes.get(1).clone()
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
        Simplify(node, true);
        boolean same = node.equals(last);
        return same;
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
