package gov.cabinetoffice.gap.applybackend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GrantApplicantOrganisationType {
    LIMITED_COMPANY("Limited company"),
    NON_LIMITED_COMPANY("Non-limited company"),
    UNLIMITED_COMPANY("Unlimited company"),
    CHARITY("Charity"),
    INDIVIDUAL("I am applying as an Individual"),
    OTHER("Other");

    private String name;

    private GrantApplicantOrganisationType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static GrantApplicantOrganisationType valueOfName(String name) {
        for (GrantApplicantOrganisationType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    @JsonCreator
    public static GrantApplicantOrganisationType getGrantApplicantOrganisationTypeFromName(String name) {

        return valueOfName(name);

    }
}
