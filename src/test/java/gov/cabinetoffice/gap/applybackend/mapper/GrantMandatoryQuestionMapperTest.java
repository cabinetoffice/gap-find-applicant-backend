package gov.cabinetoffice.gap.applybackend.mapper;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GrantMandatoryQuestionMapperTest {
    final GrantMandatoryQuestionMapper grantMandatoryQuestionMapper = Mappers.getMapper(GrantMandatoryQuestionMapper.class);

    @Nested
    class mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO {
        @Test
        void grantMandatoryQuestionIsFilled() {
            UUID uuid = UUID.randomUUID();
            final Submission submission = Submission.builder()
                    .id(uuid)
                    .build();
            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions.builder()
                    .name("name")
                    .submission(submission)
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
            assertThat(result.getOrgType()).isEqualTo(grantMandatoryQuestions.getOrgType().toString());
            assertThat(result.getCompaniesHouseNumber()).isEqualTo(grantMandatoryQuestions.getCompaniesHouseNumber());
            assertThat(result.getCharityCommissionNumber()).isEqualTo(grantMandatoryQuestions.getCharityCommissionNumber());
            assertThat(result.getFundingAmount()).isEqualTo(grantMandatoryQuestions.getFundingAmount().toString());
            assertThat(result.getFundingLocation()).isEqualTo(List.of(grantMandatoryQuestions.getFundingLocation()[0].getName()));
            assertThat(result.getSubmissionId()).isEqualTo(uuid);
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
                    .name(Optional.of("name"))
                    .addressLine1(Optional.of("addressLine1"))
                    .addressLine2(Optional.of("addressLine2"))
                    .city(Optional.of("city"))
                    .county(Optional.of("county"))
                    .postcode(Optional.of("postcode"))
                    .orgType(Optional.of("Limited company"))
                    .charityCommissionNumber(Optional.of("charityCommissionNumber"))
                    .companiesHouseNumber(Optional.of("companiesHouseNumber"))
                    .fundingAmount(Optional.of("1000"))
                    .fundingLocation(Optional.of(List.of("Scotland")))
                    .build();

            grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(updateGrantMandatoryQuestionDto, mandatoryQuestions);

            assertThat(mandatoryQuestions.getVersion()).isEqualTo(1);
            assertThat(mandatoryQuestions.getName()).isEqualTo("name");
            assertThat(mandatoryQuestions.getAddressLine1()).isEqualTo("addressLine1");
            assertThat(mandatoryQuestions.getAddressLine2()).isEqualTo("addressLine2");
            assertThat(mandatoryQuestions.getCity()).isEqualTo("city");
            assertThat(mandatoryQuestions.getCounty()).isEqualTo("county");
            assertThat(mandatoryQuestions.getPostcode()).isEqualTo("postcode");
            assertThat(mandatoryQuestions.getOrgType()).isEqualTo(GrantMandatoryQuestionOrgType.LIMITED_COMPANY);
            assertThat(mandatoryQuestions.getCompaniesHouseNumber()).isEqualTo("companiesHouseNumber");
            assertThat(mandatoryQuestions.getCharityCommissionNumber()).isEqualTo("charityCommissionNumber");
            assertThat(mandatoryQuestions.getFundingAmount()).isEqualTo(new BigDecimal("1000"));
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
                    .name(Optional.of("newName"))
                    .build();

            grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(updateGrantMandatoryQuestionDto, mandatoryQuestions);

            assertThat(mandatoryQuestions.getVersion()).isEqualTo(1);
            assertThat(mandatoryQuestions.getName()).isEqualTo("newName");
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
            final GrantMandatoryQuestions mandatoryQuestions = GrantMandatoryQuestions.builder()
                    .build();

            final GrantMandatoryQuestions result = grantMandatoryQuestionMapper.mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(null, mandatoryQuestions);

            assertThat(result).isEqualTo(mandatoryQuestions);
        }
    }

}