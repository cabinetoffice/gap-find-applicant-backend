package gov.cabinetoffice.gap.applybackend.generators;

import gov.cabinetoffice.gap.applybackend.model.GrantBeneficiary;

import java.util.UUID;

public class GrantBeneficiaryGenerator {

    public static GrantBeneficiary.GrantBeneficiaryBuilder generateRandomGrantBeneficiary() {
        return GrantBeneficiary.builder()
                .grantBeneficiaryId(UUID.fromString("55ab5fbd-0682-4d3d-a467-01c7a447f07c"))
                .schemeId(1)
                .submissionId(UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c"))
                .ageGroup1(false)
                .ageGroup2(false)
                .ageGroup3(true)
                .ageGroup4(true)
                .ageGroup5(true)
                .ageGroupAll(false);
    }
}
