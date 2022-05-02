package main.kiwitor.nomad.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {
    private static final Pattern pattern = Pattern.compile("\\d+.\\d+");

    public static double parsePercent(String percent) {
        Matcher matcher = pattern.matcher(percent);
        return Double.parseDouble(matcher.find() ? matcher.group() : "0.0") / 100;
    }
}
