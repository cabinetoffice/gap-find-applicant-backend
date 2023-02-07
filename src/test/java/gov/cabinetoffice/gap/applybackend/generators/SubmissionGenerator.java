package gov.cabinetoffice.gap.applybackend.generators;

import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantApplication;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.model.Submission;

import java.util.UUID;

public class SubmissionGenerator {
    public static Submission.SubmissionBuilder randomSubmissionGenerator() {
        return Submission.builder()
                .id(UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c"))
                .version(1)
                .applicationName("Test application name")
                .application(randomGrantApplicationGenerator().build())
                .createdBy(randomGrantApplicantGenerator().build())
                .scheme(randomGrantSchemeGenerator().build());
    }

    private static GrantScheme.GrantSchemeBuilder randomGrantSchemeGenerator() {
        return GrantScheme.builder()
                .id(1);
    }

    private static GrantApplication.GrantApplicationBuilder randomGrantApplicationGenerator() {
        return GrantApplication.builder()
                .id(1)
                .version(1);
    }

    private static GrantApplicant.GrantApplicantBuilder randomGrantApplicantGenerator() {
        return GrantApplicant.builder()
                .id(1)
                .userId(UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c"));
    }
}
