package miro.widgetservice;

public class ObjectUtils {

    public static <T> T coalesce(T a, T b) {
        return a != null ? a : b;
    }
}
