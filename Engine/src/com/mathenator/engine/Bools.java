package com.mathenator.engine;

public class Bools {
    public static boolean isNum(String c) {
        int p = 0;
        if (c.charAt(0) == '-') ++p;
        return isNum(c.charAt(p));
    }

    public static boolean isNum(char c) {
        return ((c >= 48 && c <= 57) || c == '.');
    }

    public static boolean isFn (String s) {
        s = s.toLowerCase();
        if (s.equals("sin")) return true;
        if (s.equals("cos")) return true;
        if (s.equals("tan")) return true;
        if (s.equals("asin")) return true;
        if (s.equals("acos")) return true;
        if (s.equals("atan")) return true;
        if (s.equals("ln")) return true;
        if (s.equals("log")) return true;
        if (s.equals("d")) return true;
        if (s.equals("i")) return true;
        if (s.equals("atan2")) return true;

        return false;
    }
}
