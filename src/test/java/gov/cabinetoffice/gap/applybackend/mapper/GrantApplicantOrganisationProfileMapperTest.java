package gov.cabinetoffice.gap.applybackend.mapper;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantOrganisationType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GrantApplicantOrganisationProfileMapperTest {
    private final long PROFILE_ID = 1;
    private final GrantApplicantOrganisationProfileMapper grantApplicantOrganisationProfileMapper = Mappers.getMapper(GrantApplicantOrganisationProfileMapper.class);

    @Test
    void mapOrgProfileToGrantMandatoryQuestion__OrganisationProfileIsFilled() {
        final GrantApplicantOrganisationProfile grantApplicantOrganisationProfile = GrantApplicantOrganisationProfile
                .builder()
                .id(PROFILE_ID)
                .legalName("AND Digital")
                .charityCommissionNumber("45")
                .companiesHouseNumber("000010")
                .addressLine1("AND Digital")
                .addressLine2("9 George Square")
                .town("Glasgow")
                .postcode("G2 1QQ")
                .county("Renfrewshire")
                .type(GrantApplicantOrganisationType.LIMITED_COMPANY)
                .build();

        final GrantMandatoryQuestions result = grantApplicantOrganisationProfileMapper.mapOrgProfileToGrantMandatoryQuestion(grantApplicantOrganisationProfile);

        assertThat(result.getName()).isEqualTo(grantApplicantOrganisationProfile.getLegalName());
        assertThat(result.getAddressLine1()).isEqualTo(grantApplicantOrganisationProfile.getAddressLine1());
        assertThat(result.getAddressLine2()).isEqualTo(grantApplicantOrganisationProfile.getAddressLine2());
        assertThat(result.getCounty()).isEqualTo(grantApplicantOrganisationProfile.getCounty());
        assertThat(result.getCity()).isEqualTo(grantApplicantOrganisationProfile.getTown());
        assertThat(result.getPostcode()).isEqualTo(grantApplicantOrganisationProfile.getPostcode());
        assertThat(result.getOrgType()).isEqualTo(GrantMandatoryQuestionOrgType.LIMITED_COMPANY);
        assertThat(result.getCharityCommissionNumber()).isEqualTo(grantApplicantOrganisationProfile.getCharityCommissionNumber());
        assertThat(result.getCompaniesHouseNumber()).isEqualTo(grantApplicantOrganisationProfile.getCompaniesHouseNumber());
    }

    @Test
    void mapOrgProfileToGrantMandatoryQuestion__OrganisationProfileIsPartiallyFilled() {
        final GrantApplicantOrganisationProfile grantApplicantOrganisationProfile = GrantApplicantOrganisationProfile
                .builder()
                .id(PROFILE_ID)
                .town("Glasgow")
                .build();
        final GrantMandatoryQuestions result = grantApplicantOrganisationProfileMapper.mapOrgProfileToGrantMandatoryQuestion(grantApplicantOrganisationProfile);

        assertThat(result.getName()).isNull();
        assertThat(result.getAddressLine1()).isNull();
        assertThat(result.getAddressLine2()).isNull();
        assertThat(result.getCounty()).isNull();
        assertThat(result.getCity()).isEqualTo("Glasgow");
        assertThat(result.getPostcode()).isNull();
        assertThat(result.getOrgType()).isNull();
        assertThat(result.getCharityCommissionNumber()).isNull();
        assertThat(result.getCompaniesHouseNumber()).isNull();

    }

    @Test
    void mapOrgProfileToGrantMandatoryQuestion__OrganisationProfileIsNull() {
        final GrantApplicantOrganisationProfile grantApplicantOrganisationProfile = null;
        final GrantMandatoryQuestions result = grantApplicantOrganisationProfileMapper.mapOrgProfileToGrantMandatoryQuestion(grantApplicantOrganisationProfile);

        assertThat(result).isNull();
    }
}