package com.mathenator.engine;

public class Bools {

    public static boolean isNumOrChar(String s) {
        return s.length() > 0 && isNumOrChar(s.charAt(0));
    }

    public static boolean isNumOrChar(char c) {
        return (isNum(c) || (c >= 97 && c <= 122) || (c >= 65 && c <= 90));
    }

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
        if (s.equals("D")) return true;
        if (s.equals("I")) return true;
        if (s.equals("atan2")) return true;

        return false;
    }
}
