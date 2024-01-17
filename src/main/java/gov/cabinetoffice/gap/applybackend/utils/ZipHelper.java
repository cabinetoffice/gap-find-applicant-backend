package gov.cabinetoffice.gap.applybackend.utils;

public class ZipHelper {
    public static String generateFilename(final String legalName, final String gapId) {

        if (legalName == null || gapId == null) {
            throw new RuntimeException("legalName and gapId cannot be null");
        }

        final String truncatedLegalName = legalName.length() > 50 ? legalName.substring(0, 50).trim() : legalName;
        final String cleanLegalName = truncatedLegalName
                .replace(" ", "_")
                .replaceAll("[<>:\"/\\\\?*]", "_");

        final String cleanGapId = gapId.replace("-", "_");
        return String.format("%s_%s", cleanLegalName, cleanGapId);
    }
}
