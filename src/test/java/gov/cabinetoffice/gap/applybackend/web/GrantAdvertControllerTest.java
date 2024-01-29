package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.GetContentfulAdvertExistsDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantAdvertDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.mapper.GrantMandatoryQuestionMapper;
import gov.cabinetoffice.gap.applybackend.model.*;
import gov.cabinetoffice.gap.applybackend.service.GrantAdvertService;
import gov.cabinetoffice.gap.applybackend.service.GrantApplicantService;
import gov.cabinetoffice.gap.applybackend.service.GrantMandatoryQuestionService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantAdvertControllerTest {
    private final String applicantUserId = "75ab5fbd-0682-4d3d-a467-01c7a447f07c";

    private final GrantApplicantOrganisationProfile applicantOrganisationProfile = GrantApplicantOrganisationProfile.builder()
            .id(1)
            .build();
    private final GrantApplicant applicant = GrantApplicant.builder()
            .id(1)
            .userId(applicantUserId)
            .organisationProfile(applicantOrganisationProfile)
            .build();

    private final int schemeId = 123;

    private final GrantScheme scheme = GrantScheme.builder()
            .id(schemeId)
            .build();
    @Mock
    GrantAdvertService grantAdvertService;
    @InjectMocks
    GrantAdvertController grantAdvertController;
    private JwtPayload jwtPayload;
    @Mock
    private GrantMandatoryQuestionService grantMandatoryQuestionService;
    @Mock
    private GrantApplicantService grantApplicantService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private GrantMandatoryQuestionMapper mapper;

    void setupSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        jwtPayload = JwtPayload.builder().sub(applicantUserId).build();
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(jwtPayload);
    }

    @Nested
    class generateGetGrantAdvertDtoFromAdvertSlug {

        @Test
        void generateGetGrantAdvertDtoFromAdvertSlug_happyPath() {
            setupSecurityContext();

            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions.builder().build();
            final GetGrantMandatoryQuestionDto grantMandatoryQuestionDto = GetGrantMandatoryQuestionDto.builder().build();
            final GrantAdvert grantAdvert = GrantAdvert.builder()
                    .scheme(scheme)
                    .build();
            final GetGrantAdvertDto getGrantAdvertDto = GetGrantAdvertDto.builder()
                    .grantSchemeId(schemeId)
                    .mandatoryQuestionsDto(grantMandatoryQuestionDto)
                    .build();


            when(grantApplicantService.getApplicantById(applicantUserId))
                    .thenReturn(applicant);
            when(grantAdvertService.getAdvertByContentfulSlug("slug"))
                    .thenReturn(grantAdvert);
            when(grantMandatoryQuestionService.mandatoryQuestionExistsBySchemeIdAndApplicantId(scheme.getId(), applicant.getId()))
                    .thenReturn(true);
            when(grantMandatoryQuestionService.getMandatoryQuestionBySchemeId(123, applicantUserId))
                    .thenReturn(grantMandatoryQuestions);
            when(mapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions))
                    .thenReturn(grantMandatoryQuestionDto);
            when(grantAdvertService.generateGetGrantAdvertDto(grantAdvert, grantMandatoryQuestionDto))
                    .thenReturn(getGrantAdvertDto);

            final GetGrantAdvertDto result = grantAdvertController.generateGetGrantAdvertDtoFromAdvertSlug("slug").getBody();

            assertThat(result).isEqualTo(getGrantAdvertDto);
        }

        @Test
        void generateGetGrantAdvertDtoFromAdvertSlug_HandlesNotFoundException() {
            setupSecurityContext();

            final String contentfulSlug = "chargepoint-grant-for-homeowners-1";
            final GetGrantAdvertDto getGrantAdvertDto = GetGrantAdvertDto.builder()
                    .isAdvertInDatabase(false)
                    .build();
            when(grantApplicantService.getApplicantById(applicantUserId))
                    .thenReturn(applicant);
            when(grantAdvertService.getAdvertByContentfulSlug(contentfulSlug))
                    .thenThrow(NotFoundException.class);

            final GetGrantAdvertDto result = grantAdvertController.generateGetGrantAdvertDtoFromAdvertSlug(contentfulSlug).getBody();

            assertThat(result).isEqualTo(getGrantAdvertDto);
        }

        @Test
        void generateGetGrantAdvertDtoFromAdvertSlug_ThrowsAnyOtherKindOfException() {
            setupSecurityContext();
            final String contentfulSlug = "chargepoint-grant-for-homeowners-1";
            when(grantApplicantService.getApplicantById(applicantUserId))
                    .thenReturn(applicant);
            when(grantAdvertService.getAdvertByContentfulSlug(contentfulSlug))
                    .thenThrow(IllegalArgumentException.class);

            assertThrows(IllegalArgumentException.class, () -> grantAdvertController.generateGetGrantAdvertDtoFromAdvertSlug(contentfulSlug));
        }
    }

    @Nested
    class advertExistsInContentful {
        @Test
        void advertExistsInContentful_ReturnsExpectedData() {

            final String advertSlug = "chargepoint-grant-for-homeowners-1";

            when(grantAdvertService.advertExistsInContentful(advertSlug))
                    .thenReturn(true);

            final ResponseEntity<GetContentfulAdvertExistsDto> methodResponse = grantAdvertController.advertExistsInContentful(advertSlug);

            assertThat(methodResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            final GetContentfulAdvertExistsDto responseBody = methodResponse.getBody();

            assertThat(responseBody.isAdvertInContentful()).isTrue();
        }
    }

    @Nested
    class generateGetGrantAdvertDtoFromSchemeId {
        @Test
        void generateGetGrantAdvertDtoFromSchemeId() {
            setupSecurityContext();

            final GrantMandatoryQuestions grantMandatoryQuestions = GrantMandatoryQuestions.builder().build();
            final GetGrantMandatoryQuestionDto grantMandatoryQuestionDto = GetGrantMandatoryQuestionDto.builder().build();
            final GrantAdvert grantAdvert = GrantAdvert.builder()
                    .scheme(scheme)
                    .build();
            final GetGrantAdvertDto getGrantAdvertDto = GetGrantAdvertDto.builder()
                    .grantSchemeId(schemeId)
                    .mandatoryQuestionsDto(grantMandatoryQuestionDto)
                    .build();


            when(grantApplicantService.getApplicantById(applicantUserId))
                    .thenReturn(applicant);
            when(grantAdvertService.getAdvertBySchemeId(String.valueOf(schemeId)))
                    .thenReturn(grantAdvert);
            when(grantMandatoryQuestionService.mandatoryQuestionExistsBySchemeIdAndApplicantId(scheme.getId(), applicant.getId()))
                    .thenReturn(true);
            when(grantMandatoryQuestionService.getMandatoryQuestionBySchemeId(123, applicantUserId))
                    .thenReturn(grantMandatoryQuestions);
            when(mapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions))
                    .thenReturn(grantMandatoryQuestionDto);
            when(grantAdvertService.generateGetGrantAdvertDto(grantAdvert, grantMandatoryQuestionDto))
                    .thenReturn(getGrantAdvertDto);

            final GetGrantAdvertDto result = grantAdvertController.generateGetGrantAdvertDtoFromSchemeId(String.valueOf(schemeId)).getBody();

            assertThat(result).isEqualTo(getGrantAdvertDto);
        }

        @Test
        void generateGetGrantAdvertDtoFromSchemeId_ThrowsAnyOtherKindOfException() {
            setupSecurityContext();
            when(grantApplicantService.getApplicantById(applicantUserId))
                    .thenReturn(applicant);
            when(grantAdvertService.getAdvertBySchemeId(String.valueOf(schemeId)))
                    .thenThrow(IllegalArgumentException.class);

            assertThrows(IllegalArgumentException.class, () -> grantAdvertController.generateGetGrantAdvertDtoFromSchemeId(String.valueOf(schemeId)));
        }
    }

    @Nested
    class validateGrantWebpageUrl {
        @Test
        void validatesGrantWebpageUrl_returnsSuccessWithValidArgs(){
            final String grantWebpageUrl = "https://www.example.com/external-advert";
            final String contentfulSlug = "internal-contentful-slug";
            doNothing().when(grantAdvertService).validateGrantWebpageUrl(grantWebpageUrl, contentfulSlug);
            ResponseEntity<String> response = grantAdvertController.validateGrantWebpageUrl(grantWebpageUrl, contentfulSlug);

            assertThat(response).isEqualTo(ResponseEntity.ok("Success"));
        }

        @Test
        void validatesGrantWebpageUrl_returnsNotFound(){
            final String grantWebpageUrl = "https://www.maliciousdomain.com/extenal";
            final String contentfulSlug = "internal-contentful-slug";
            doThrow(NotFoundException.class).when(grantAdvertService).validateGrantWebpageUrl(grantWebpageUrl, contentfulSlug);
            assertThrows(NotFoundException.class, ()-> grantAdvertController.validateGrantWebpageUrl(grantWebpageUrl, contentfulSlug));
        }

        @Test
        void validatesGrantWebpageUrl_ThrowsNotFoundException(){
            final String grantWebpageUrl = "https://www.maliciousdomain.com/extenal";
            final String contentfulSlug = "internal-contentful-slug";
            doThrow(NotFoundException.class).when(grantAdvertService).validateGrantWebpageUrl(grantWebpageUrl, contentfulSlug);
            assertThrows(NotFoundException.class, ()-> grantAdvertController.validateGrantWebpageUrl(grantWebpageUrl, contentfulSlug));
        }
    }
}