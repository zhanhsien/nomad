package main.kiwitor.nomad.util;

import java.util.Objects;

public class HtmlUtils {
    public static double parsePercent(String percent) {
        return Double.parseDouble(Objects.requireNonNull(percent).replaceAll("%", "")) / 100;
    }
}
