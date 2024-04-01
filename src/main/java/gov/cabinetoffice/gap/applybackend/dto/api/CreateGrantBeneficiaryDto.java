package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Builder
@Data
public class CreateGrantBeneficiaryDto {
    @NotNull
    private UUID submissionId;

    private Boolean locationNeEng;
    private Boolean locationNwEng;
    private Boolean locationSeEng;
    private Boolean locationSwEng;
    private Boolean locationMidEng;
    private Boolean locationSco;
    private Boolean locationWal;
    private Boolean locationNir;
    private Boolean locationLon;
    private Boolean locationOutUk;

    @NotNull(message = "Select 'Yes, answer the equality questions' or 'No, skip the equality questions'")
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
    private Boolean organisationGroup1;
    private Boolean organisationGroup2;
    private Boolean organisationGroup3;

    @Size(max = 100, message = "Other ethnic group details can not be longer than 100 characters")
    private String ethnicOtherDetails;

    private Boolean ethnicGroupAll;
    private Boolean supportingDisabilities;
    private Boolean sexualOrientationGroup1;
    private Boolean sexualOrientationGroup2;
    private Boolean sexualOrientationGroup3;
    private Boolean sexualOrientationOther;

    @Size(max = 100, message = "Other sexual orientation details can not be longer than 100 characters")
    private String sexualOrientationOtherDetails;

    private Boolean sexualOrientationGroupAll;
    private Boolean sexGroup1;
    private Boolean sexGroup2;
    private Boolean sexGroupAll;
}
