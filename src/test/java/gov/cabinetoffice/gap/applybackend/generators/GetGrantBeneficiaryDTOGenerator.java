package gov.cabinetoffice.gap.applybackend.generators;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantBeneficiaryDto;

import java.util.UUID;

public class GetGrantBeneficiaryDTOGenerator {

    public static GetGrantBeneficiaryDto.GetGrantBeneficiaryDtoBuilder generateRandomGetGrantBeneficiaryDTO() {
        return GetGrantBeneficiaryDto.builder()
                .grantBeneficiaryId(UUID.fromString("55ab5fbd-0682-4d3d-a467-01c7a447f07c"))
                .submissionId(UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c"))
                .locationNeEng(true)
                .locationNwEng(true)
                .locationSeEng(true)
                .locationSwEng(true)
                .locationMidEng(true)
                .locationSco(true)
                .locationWal(true)
                .hasProvidedAdditionalAnswers(true)
                .ageGroup1(true)
                .ageGroup2(true)
                .ageGroup3(true)
                .ageGroup4(true)
                .ageGroup5(true)
                .ageGroupAll(true)
                .ethnicGroup1(true)
                .ethnicGroup2(true)
                .ethnicGroup3(true)
                .ethnicGroup4(true)
                .ethnicGroup5(true)
                .ethnicGroupOther(true)
                .ethnicOtherDetails("Other ethnic datils")
                .ethnicGroupAll(true)
                .organisationGroup1(true)
                .organisationGroup2(true)
                .organisationGroup3(true)
                .supportingDisabilities(true)
                .sexualOrientationGroup1(true)
                .sexualOrientationGroup2(true)
                .sexualOrientationGroup3(true)
                .sexualOrientationOther(true)
                .sexualOrientationOtherDetails("Orientation Details")
                .sexualOrientationGroupAll(true)
                .sexGroup1(true)
                .sexGroup2(true)
                .sexGroupAll(true);
    }
}
