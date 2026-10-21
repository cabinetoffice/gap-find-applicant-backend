package gov.cabinetoffice.gap.applybackend.constants;

import java.util.List;
import java.util.regex.Pattern;

public final class ValidationConstants {

    private ValidationConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final int SHORT_TEXT_MAX_LENGTH_DEFAULT = 250;
    public static final int SHORT_TEXT_MIN_LENGTH_DEFAULT = 1;
    public static final int POSTCODE_MAX_LENGTH = 8;
    public static final int POSTCODE_ARRAY_POSITION = 4;

    public static final String SINGLE_RESPONSE_FIELD = "response";
    public static final String MULTI_RESPONSE_FIELD = "multiResponse";

    // Keep in step with UnsupportedCharacterHelper.UNSUPPORTED_CHARACTER_PATTERNS in gap-find-lambda-submission-export.
    // The first three patterns corrupt the submission ODT: odfdom writes characters above U+FFFF as two
    // surrogate character references (e.g. &#55357;&#56832;), which are not valid XML.
    // The last blocks emoji below U+FFFF (Unicode Emoji_Presentation=Yes, plus the U+FE0F emoji variation
    // selector and U+20E3 keycap) so they are treated the same as other emoji. Text symbols such as ✓ ★ © stay allowed.
    public static final List<Pattern> UNSUPPORTED_CHARACTER_PATTERNS = List.of(
            Pattern.compile("[\\x{10000}-\\x{10FFFF}]"),
            Pattern.compile("[\\uD800-\\uDFFF]"),
            Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\uFFFE\\uFFFF]"),
            Pattern.compile("[\\uFE0F\\u20E3\\u231A\\u231B\\u23E9-\\u23EC\\u23F0\\u23F3\\u25FD\\u25FE\\u2614\\u2615"
                    + "\\u2648-\\u2653\\u267F\\u2693\\u26A1\\u26AA\\u26AB\\u26BD\\u26BE\\u26C4\\u26C5\\u26CE\\u26D4"
                    + "\\u26EA\\u26F2\\u26F3\\u26F5\\u26FA\\u26FD\\u2705\\u270A\\u270B\\u2728\\u274C\\u274E"
                    + "\\u2753-\\u2755\\u2757\\u2795-\\u2797\\u27B0\\u27BF\\u2B1B\\u2B1C\\u2B50\\u2B55]")
    );
}
