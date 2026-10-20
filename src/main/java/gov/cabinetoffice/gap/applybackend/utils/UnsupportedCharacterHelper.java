package gov.cabinetoffice.gap.applybackend.utils;

import java.util.regex.Pattern;

import static gov.cabinetoffice.gap.applybackend.constants.ValidationConstants.UNSUPPORTED_CHARACTER_PATTERNS;

public class UnsupportedCharacterHelper {

    private UnsupportedCharacterHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean containsUnsupportedCharacters(final String value) {
        return value != null && UNSUPPORTED_CHARACTER_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(value).find());
    }

    public static String stripUnsupportedCharacters(final String value) {
        if (value == null) {
            return null;
        }

        String stripped = value;
        for (final Pattern pattern : UNSUPPORTED_CHARACTER_PATTERNS) {
            stripped = pattern.matcher(stripped).replaceAll("");
        }
        return stripped;
    }
}
