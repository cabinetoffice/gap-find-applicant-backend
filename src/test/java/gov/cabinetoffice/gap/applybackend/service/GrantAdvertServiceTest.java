package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrandAdvertDto;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvert;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertPageResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertQuestionResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertSectionResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantAdvertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantAdvertServiceTest {

    @Mock
    GrantAdvertRepository grantAdvertRepository;
    @Mock
    GrantApplicationService grantApplicationService;

    @InjectMocks
    GrantAdvertService grantAdvertService;
    private final UUID ADVERT_ID = UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c");

    @Test
    void getAdvertByContentfulSlug_createDtoForInternalApplicationAndVersion1() {
        final GrantAdvertResponse response = generateResponseWithNoHowToApplySection();
        final GrantScheme scheme = GrantScheme.builder().id(1).build();
        final GrantAdvert advert = GrantAdvert.builder()
                .contentfulSlug("slug")
                .id(ADVERT_ID)
                .version(1)
                .scheme(scheme)
                .response(response)
                .build();

        when(grantAdvertRepository.findByContentfulSlug("slug")).thenReturn(Optional.of(advert));
        when(grantApplicationService.doesSchemeHaveApplication(scheme)).thenReturn(true);
        when(grantApplicationService.getGrantApplicationId(scheme)).thenReturn(1);

        final GetGrandAdvertDto methodResponse = grantAdvertService.getAdvertByContentfulSlug("slug");

        assertThat(methodResponse.getId()).isEqualTo(ADVERT_ID);
        assertThat(methodResponse.getVersion()).isEqualTo(1);
        assertThat(methodResponse.getExternalSubmissionUrl()).isEmpty();
        assertThat(methodResponse.isInternal()).isTrue();
        assertThat(methodResponse.getGrantApplicationId()).isEqualTo(1);
        assertThat(methodResponse.getGrantSchemeId()).isEqualTo(1);
    }

    @Test
    void getAdvertByContentfulSlug_createDtoForInternalApplicationAndVersion2() {
        final GrantAdvertResponse response = generateResponseWithNoHowToApplySection();
        final GrantScheme scheme = GrantScheme.builder().id(1).build();
        final GrantAdvert advert = GrantAdvert.builder()
                .contentfulSlug("slug")
                .id(ADVERT_ID)
                .version(2)
                .scheme(scheme)
                .response(response)
                .build();

        when(grantAdvertRepository.findByContentfulSlug("slug")).thenReturn(Optional.of(advert));
        when(grantApplicationService.doesSchemeHaveApplication(scheme)).thenReturn(true);
        when(grantApplicationService.getGrantApplicationId(scheme)).thenReturn(1);

        final GetGrandAdvertDto methodResponse = grantAdvertService.getAdvertByContentfulSlug("slug");

        assertThat(methodResponse.getId()).isEqualTo(ADVERT_ID);
        assertThat(methodResponse.getVersion()).isEqualTo(2);
        assertThat(methodResponse.getExternalSubmissionUrl()).isEmpty();
        assertThat(methodResponse.isInternal()).isTrue();
        assertThat(methodResponse.getGrantApplicationId()).isEqualTo(1);
        assertThat(methodResponse.getGrantSchemeId()).isEqualTo(1);
    }

    @Test
    void getAdvertByContentfulSlug_createDtoForExternalApplicationAndVersion2() {
        final GrantAdvertResponse response = genereteResponseWithHowToApplySection();
        final GrantScheme scheme = GrantScheme.builder().id(1).build();
        final GrantAdvert advert = GrantAdvert.builder()
                .contentfulSlug("slug")
                .id(ADVERT_ID)
                .version(2)
                .scheme(scheme)
                .response(response)
                .build();

        when(grantAdvertRepository.findByContentfulSlug("slug")).thenReturn(Optional.of(advert));
        when(grantApplicationService.doesSchemeHaveApplication(scheme)).thenReturn(false);
        when(grantApplicationService.getGrantApplicationId(scheme)).thenReturn(null);

        final GetGrandAdvertDto methodResponse = grantAdvertService.getAdvertByContentfulSlug("slug");

        assertThat(methodResponse.getId()).isEqualTo(ADVERT_ID);
        assertThat(methodResponse.getVersion()).isEqualTo(2);
        assertThat(methodResponse.getExternalSubmissionUrl()).isEqualTo("responseUrl");
        assertThat(methodResponse.isInternal()).isFalse();
        assertThat(methodResponse.getGrantApplicationId()).isNull();
        assertThat(methodResponse.getGrantSchemeId()).isEqualTo(1);
    }

    @Test
    void getAdvertByContentfulSlug_createDtoForExternalApplicationAndVersion1() {
        final GrantAdvertResponse response = genereteResponseWithHowToApplySection();
        final GrantScheme scheme = GrantScheme.builder().id(1).build();
        final GrantAdvert advert = GrantAdvert.builder()
                .contentfulSlug("slug")
                .id(ADVERT_ID)
                .version(1)
                .scheme(scheme)
                .response(response)
                .build();

        when(grantAdvertRepository.findByContentfulSlug("slug")).thenReturn(Optional.of(advert));
        when(grantApplicationService.doesSchemeHaveApplication(scheme)).thenReturn(false);
        when(grantApplicationService.getGrantApplicationId(scheme)).thenReturn(null);

        final GetGrandAdvertDto methodResponse = grantAdvertService.getAdvertByContentfulSlug("slug");

        assertThat(methodResponse.getId()).isEqualTo(ADVERT_ID);
        assertThat(methodResponse.getVersion()).isEqualTo(1);
        assertThat(methodResponse.getExternalSubmissionUrl()).isEqualTo("responseUrl");
        assertThat(methodResponse.isInternal()).isFalse();
        assertThat(methodResponse.getGrantApplicationId()).isNull();
        assertThat(methodResponse.getGrantSchemeId()).isEqualTo(1);
    }

    @Test
    void getExternalSubmissionUrl() {
        final GrantAdvertResponse response = genereteResponseWithHowToApplySection();
        final GrantAdvert advert = GrantAdvert.builder()
                .response(response)
                .build();

        final String methodResponse = GrantAdvertService.getExternalSubmissionUrl(advert);

        assertThat(methodResponse).isEqualTo("responseUrl");
    }
    @Test
    void getExternalSubmissionUrl_returnEmptyString() {
        final GrantAdvertResponse response = generateResponseWithNoHowToApplySection();
        final GrantAdvert advert = GrantAdvert.builder()
                .response(response)
                .build();

        final String methodResponse = GrantAdvertService.getExternalSubmissionUrl(advert);

        assertThat(methodResponse).isEmpty();
    }

    private static GrantAdvertResponse genereteResponseWithHowToApplySection() {
        final GrantAdvertQuestionResponse questionResponse = GrantAdvertQuestionResponse.builder()
                .id("grantWebpageUrl")
                .response("responseUrl")
                .build();
        final GrantAdvertPageResponse pageResponse = GrantAdvertPageResponse.builder()
                .id("pageId")
                .questions(List.of(questionResponse))
                .build();
        final GrantAdvertSectionResponse sectionResponse = GrantAdvertSectionResponse.builder()
                .id("howToApply")
                .pages(List.of(pageResponse))
                .build();
        final GrantAdvertResponse response = GrantAdvertResponse.builder()
                .sections(List.of(sectionResponse))
                .build();
        return response;
    }
    private static GrantAdvertResponse generateResponseWithNoHowToApplySection() {
        final GrantAdvertSectionResponse sectionResponse = GrantAdvertSectionResponse.builder()
                .id("")
                .build();
        final GrantAdvertResponse response = GrantAdvertResponse.builder()
                .sections(List.of(sectionResponse))
                .build();
        return response;
    }
}