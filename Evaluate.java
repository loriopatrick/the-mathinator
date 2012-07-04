import java.util.ArrayList;

public class Evaluate {

    public static boolean isNumOrChar(String s) {
        return s.length() > 0 && isNumOrChar(s.charAt(0));
    }

    public static boolean isNumOrChar(char c) {
        return (isNum(c) || (c >= 97 && c <= 122) || (c >= 65 && c <= 90));
    }

    public static boolean isNum(String c) {
        return isNum(c.charAt(0));
    }

    public static boolean isNum(char c) {
        return ((c >= 48 && c <= 57) || c == '-' || c == '.');
    }
}
