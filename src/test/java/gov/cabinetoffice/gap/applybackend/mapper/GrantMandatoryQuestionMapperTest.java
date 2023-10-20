package gov.cabinetoffice.gap.applybackend.mapper;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GrantMandatoryQuestionMapperTest {
    final GrantMandatoryQuestionMapper grantMandatoryQuestionMapper = Mappers.getMapper(GrantMandatoryQuestionMapper.class);

    @Nested
    class mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO {
        @Test
        void grantMandatoryQuestionIsFilled() {
            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions.builder()
                    .name("name")
                    .addressLine1("addressLine1")
                    .addressLine2("addressLine2")
                    .city("city")
                    .county("county")
                    .postcode("postcode")
                    .orgType(GrantMandatoryQuestionOrgType.LIMITED_COMPANY)
                    .companiesHouseNumber("companiesHouseNumber")
                    .charityCommissionNumber("charityCommissionNumber")
                    .fundingAmount(new BigDecimal("1000"))
                    .fundingLocation(new GrantMandatoryQuestionFundingLocation[]{GrantMandatoryQuestionFundingLocation.SCOTLAND})
                    .build();

            final GetGrantMandatoryQuestionDto result = grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions);

            assertThat(result.getName()).isEqualTo(grantMandatoryQuestions.getName());
            assertThat(result.getAddressLine1()).isEqualTo(grantMandatoryQuestions.getAddressLine1());
            assertThat(result.getAddressLine2()).isEqualTo(grantMandatoryQuestions.getAddressLine2());
            assertThat(result.getCity()).isEqualTo(grantMandatoryQuestions.getCity());
            assertThat(result.getCounty()).isEqualTo(grantMandatoryQuestions.getCounty());
            assertThat(result.getPostcode()).isEqualTo(grantMandatoryQuestions.getPostcode());
            assertThat(result.getOrgType()).isEqualTo(grantMandatoryQuestions.getOrgType().name());
            assertThat(result.getCompaniesHouseNumber()).isEqualTo(grantMandatoryQuestions.getCompaniesHouseNumber());
            assertThat(result.getCharityCommissionNumber()).isEqualTo(grantMandatoryQuestions.getCharityCommissionNumber());
            assertThat(result.getFundingAmount()).isEqualTo(grantMandatoryQuestions.getFundingAmount().toString());
            assertThat(result.getFundingLocation()).isEqualTo(List.of(grantMandatoryQuestions.getFundingLocation()[0].name()));
        }

        @Test
        void grantMandatoryQuestionIsPartiallyFilled() {
            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions.builder()
                    .name("name")
                    .build();

            final GetGrantMandatoryQuestionDto result = grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions);

            assertThat(result.getName()).isEqualTo(grantMandatoryQuestions.getName());
            assertThat(result.getAddressLine1()).isNull();
            assertThat(result.getAddressLine2()).isNull();
            assertThat(result.getCity()).isNull();
            assertThat(result.getCounty()).isNull();
            assertThat(result.getPostcode()).isNull();
            assertThat(result.getOrgType()).isNull();
            assertThat(result.getCompaniesHouseNumber()).isNull();
            assertThat(result.getCharityCommissionNumber()).isNull();
            assertThat(result.getFundingAmount()).isNull();
            assertThat(result.getFundingLocation()).isNull();
        }

        @Test
        void GrantMandatoryQuestionsIsNull() {
            final GrantMandatoryQuestions mandatoryQuestions = null;

            final GetGrantMandatoryQuestionDto result = grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(mandatoryQuestions);

            assertThat(result).isNull();
        }
    }


    @Nested
    class mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion {
        @Test
        void UpdateGrantMandatoryQuestionDtoIsFilled() {
            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .version(1)
                    .build();
            final UpdateGrantMandatoryQuestionDto updateGrantMandatoryQuestionDto = UpdateGrantMandatoryQuestionDto.builder()
                    .name("name")
                    .addressLine1("addressLine1")
                    .addressLine2("addressLine2")
                    .city("city")
                    .county("county")
                    .postcode("postcode")
                    .orgType("Limited company")
                    .charityCommissionNumber("charityCommissionNumber")
                    .companiesHouseNumber("companiesHouseNumber")
                    .fundingAmount("1000")
                    .fundingLocation(List.of("Scotland"))
                    .build();

            grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(updateGrantMandatoryQuestionDto, mandatoryQuestions);

            assertThat(mandatoryQuestions.getVersion()).isEqualTo(1);
            assertThat(mandatoryQuestions.getName()).isEqualTo(updateGrantMandatoryQuestionDto.getName());
            assertThat(mandatoryQuestions.getAddressLine1()).isEqualTo(updateGrantMandatoryQuestionDto.getAddressLine1());
            assertThat(mandatoryQuestions.getAddressLine2()).isEqualTo(updateGrantMandatoryQuestionDto.getAddressLine2());
            assertThat(mandatoryQuestions.getCity()).isEqualTo(updateGrantMandatoryQuestionDto.getCity());
            assertThat(mandatoryQuestions.getCounty()).isEqualTo(updateGrantMandatoryQuestionDto.getCounty());
            assertThat(mandatoryQuestions.getPostcode()).isEqualTo(updateGrantMandatoryQuestionDto.getPostcode());
            assertThat(mandatoryQuestions.getOrgType()).isEqualTo(GrantMandatoryQuestionOrgType.LIMITED_COMPANY);
            assertThat(mandatoryQuestions.getCompaniesHouseNumber()).isEqualTo(updateGrantMandatoryQuestionDto.getCompaniesHouseNumber());
            assertThat(mandatoryQuestions.getCharityCommissionNumber()).isEqualTo(updateGrantMandatoryQuestionDto.getCharityCommissionNumber());
            assertThat(mandatoryQuestions.getFundingAmount()).isEqualTo(new BigDecimal(updateGrantMandatoryQuestionDto.getFundingAmount()));
            assertThat(mandatoryQuestions.getFundingLocation()).isEqualTo(new GrantMandatoryQuestionFundingLocation[]{GrantMandatoryQuestionFundingLocation.SCOTLAND});
        }

        @Test
        void UpdateGrantMandatoryQuestionDtoIsPartiallyFilled_DtoNullValuesShouldNotOverwriteEntity() {
            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .version(1)
                    .name("name")
                    .addressLine1("addressLine1")
                    .fundingAmount(new BigDecimal("1000"))
                    .orgType(GrantMandatoryQuestionOrgType.LIMITED_COMPANY)
                    .fundingLocation(new GrantMandatoryQuestionFundingLocation[]{GrantMandatoryQuestionFundingLocation.SCOTLAND})
                    .build();
            final UpdateGrantMandatoryQuestionDto updateGrantMandatoryQuestionDto = UpdateGrantMandatoryQuestionDto.builder()
                    .name("newName")
                    .build();

            grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(updateGrantMandatoryQuestionDto, mandatoryQuestions);

            assertThat(mandatoryQuestions.getVersion()).isEqualTo(1);
            assertThat(mandatoryQuestions.getName()).isEqualTo(updateGrantMandatoryQuestionDto.getName());
            assertThat(mandatoryQuestions.getAddressLine1()).isEqualTo("addressLine1");
            assertThat(mandatoryQuestions.getAddressLine2()).isNull();
            assertThat(mandatoryQuestions.getCity()).isNull();
            assertThat(mandatoryQuestions.getCounty()).isNull();
            assertThat(mandatoryQuestions.getPostcode()).isNull();
            assertThat(mandatoryQuestions.getOrgType()).isEqualTo(GrantMandatoryQuestionOrgType.LIMITED_COMPANY);
            assertThat(mandatoryQuestions.getCompaniesHouseNumber()).isNull();
            assertThat(mandatoryQuestions.getCharityCommissionNumber()).isNull();
            assertThat(mandatoryQuestions.getFundingAmount()).isEqualTo(new BigDecimal("1000"));
            assertThat(mandatoryQuestions.getFundingLocation()).isEqualTo(new GrantMandatoryQuestionFundingLocation[]{GrantMandatoryQuestionFundingLocation.SCOTLAND});
        }

        @Test
        void UpdateDtoIsNull() {
            final UpdateGrantMandatoryQuestionDto updateGrantMandatoryQuestionDto = null;
            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .build();

            final GrantMandatoryQuestions result = grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(updateGrantMandatoryQuestionDto, mandatoryQuestions);

            assertThat(result).isEqualTo(mandatoryQuestions);
        }
    }

}