package gov.cabinetoffice.gap.applybackend.utils;

public class ZipHelper {
    public static String generateFilename(final String legalName) {

        if (legalName == null) {
            throw new RuntimeException("legalName cannot be null");
        }

        final String truncatedLegalName = legalName.length() > 50 ? legalName.substring(0, 50).trim() : legalName;
        return truncatedLegalName
                .replace(" ", "_")
                .replaceAll("[<>:\"/\\\\?*]", "_");
    }
}
