package echo.util;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static String orEmpty(String s) {
        return s == null ? "" : s;
    }
}
