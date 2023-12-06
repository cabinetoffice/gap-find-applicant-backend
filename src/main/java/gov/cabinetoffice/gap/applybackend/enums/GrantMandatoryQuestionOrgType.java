package gov.cabinetoffice.gap.applybackend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GrantMandatoryQuestionOrgType {
    LIMITED_COMPANY("Limited company"),
    NON_LIMITED_COMPANY("Non-limited company"),
    REGISTERED_CHARITY("Registered charity"),
    UNREGISTERED_CHARITY("Unregistered charity"),
    CHARITY("Charity"),
    INDIVIDUAL("I am applying as an individual"),
    OTHER("Other");

    private String name;

    private GrantMandatoryQuestionOrgType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static GrantMandatoryQuestionOrgType valueOfName(String name) {
        for (GrantMandatoryQuestionOrgType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    @JsonCreator
    public static GrantMandatoryQuestionOrgType getGrantApplicantOrganisationTypeFromName(String name) {

        return valueOfName(name);

    }
}
