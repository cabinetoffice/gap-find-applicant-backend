package gov.cabinetoffice.gap.applybackend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GrantMandatoryQuestionFundingLocation {
    NORTH_EAST_ENGLAND("North East (England)"),
    NORTH_WEST_ENGLAND("North West (England)"),
    YORKSHIRE_AND_THE_HUMBER("Yorkshire and the Humber"),
    EAST_MIDLANDS_ENGLAND("East Midlands (England)"),
    WEST_MIDLANDS("West Midlands (England)"),
    EAST_ENGLAND("East (England)"),
    LONDON("London"),
    SOUTH_EAST_ENGLAND("South East (England)"),
    SOUTH_WEST_ENGLAND("South West (England)"),
    MIDLANDS("Midlands"),
    SCOTLAND("Scotland"),
    WALES("Wales"),
    NORTHERN_IRELAND("Northern Ireland"),
    OUTSIDE_UK("Outside of the UK");

    private final String name;

    GrantMandatoryQuestionFundingLocation(String name) {
        this.name = name;
    }

    public static GrantMandatoryQuestionFundingLocation valueOfName(String name) {
        for (GrantMandatoryQuestionFundingLocation type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    @JsonCreator
    public static GrantMandatoryQuestionFundingLocation getGrantApplicantOrganisationTypeFromName(String name) {

        return valueOfName(name);

    }

    public String getName() {
        return name;
    }

}
