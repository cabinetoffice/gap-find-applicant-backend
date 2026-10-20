package gov.cabinetoffice.gap.applybackend.utils;

import org.apache.commons.lang3.StringUtils;

public class ZipHelper {
    private static final String DEFAULT_FILENAME = "submission";

    public static String generateFilename(final String legalName) {

        if (legalName == null) {
            throw new RuntimeException("legalName cannot be null");
        }

        final String cleanedLegalName = StringUtils.normalizeSpace(
                UnsupportedCharacterHelper.stripUnsupportedCharacters(legalName));
        if (cleanedLegalName.isEmpty()) {
            return DEFAULT_FILENAME;
        }

        final String truncatedLegalName = cleanedLegalName.length() > 50 ? cleanedLegalName.substring(0, 50).trim() : cleanedLegalName;
        return truncatedLegalName
                .replace(" ", "_")
                .replaceAll("[<>:\"/\\\\?*]", "_");
    }
}
