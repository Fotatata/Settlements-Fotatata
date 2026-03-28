package dev.breezes.settlements.shared.util;

import javax.annotation.Nonnull;
import java.util.Locale;

public final class StringUtil {

    /**
     * Converts a string to title case (first letter of each word capitalized)
     */
    public static String titleCase(@Nonnull String input) {
        if (input.isEmpty()) {
            return input;
        }
        String lower = input.toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder(lower.length());
        boolean capitalizeNext = true;
        for (char c : lower.toCharArray()) {
            if (c == ' ') {
                sb.append(c);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                sb.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
