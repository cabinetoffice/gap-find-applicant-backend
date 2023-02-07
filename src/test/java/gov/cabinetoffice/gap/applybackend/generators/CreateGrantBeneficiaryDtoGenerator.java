package gov.cabinetoffice.gap.applybackend.generators;

import gov.cabinetoffice.gap.applybackend.dto.api.CreateGrantBeneficiaryDto;

import java.util.UUID;

public class CreateGrantBeneficiaryDtoGenerator {

    public static CreateGrantBeneficiaryDto.CreateGrantBeneficiaryDtoBuilder generateRandomGrantBeneficiaryDTO() {
        return CreateGrantBeneficiaryDto.builder()
                .submissionId(UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c"))
                .ageGroup1(false)
                .ageGroup2(false)
                .ageGroup3(true)
                .ageGroup4(true)
                .ageGroup5(true)
                .ageGroupAll(false);
    }
}
