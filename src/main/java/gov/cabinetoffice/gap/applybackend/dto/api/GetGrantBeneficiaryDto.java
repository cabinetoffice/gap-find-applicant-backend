package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetGrantBeneficiaryDto {
    private UUID grantBeneficiaryId;
    private UUID submissionId;
    private Boolean locationNeEng;
    private Boolean locationNwEng;
    private Boolean locationSeEng;
    private Boolean locationSwEng;
    private Boolean locationMidEng;
    private Boolean locationSco;
    private Boolean locationWal;
    private Boolean locationNir;
    private Boolean hasProvidedAdditionalAnswers;
    private Boolean ageGroup1;
    private Boolean ageGroup2;
    private Boolean ageGroup3;
    private Boolean ageGroup4;
    private Boolean ageGroup5;
    private Boolean ageGroupAll;
    private Boolean ethnicGroup1;
    private Boolean ethnicGroup2;
    private Boolean ethnicGroup3;
    private Boolean ethnicGroup4;
    private Boolean ethnicGroup5;
    private Boolean ethnicGroupOther;
    private String ethnicOtherDetails;
    private Boolean ethnicGroupAll;
    private Boolean supportingDisabilities;
    private Boolean sexualOrientationGroup1;
    private Boolean sexualOrientationGroup2;
    private Boolean sexualOrientationGroup3;
    private Boolean sexualOrientationOther;
    private String sexualOrientationOtherDetails;
    private Boolean sexualOrientationGroupAll;
    private Boolean sexGroup1;
    private Boolean sexGroup2;
    private Boolean sexGroupAll;
}
