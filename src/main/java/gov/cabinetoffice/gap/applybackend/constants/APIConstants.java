package gov.cabinetoffice.gap.applybackend.constants;

import java.util.UUID;

public final class APIConstants {
    private APIConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final UUID APPLICANT_ID = UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c");
    public static final String NAVIGATION_SECTION_ID = "sectionId";
    public static final String NAVIGATION_QUESTION_ID = "questionId";
    public static final String NAVIGATION_SECTION_LIST = "sectionList";
}
