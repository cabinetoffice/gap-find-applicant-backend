package gov.cabinetoffice.gap.applybackend.constants;

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
}
