package gov.cabinetoffice.gap.applybackend.service;

import com.contentful.java.cda.*;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrandAdvertDto;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvert;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertPageResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertQuestionResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertSectionResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantAdvertRepository;
import net.bytebuddy.pool.TypePool;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class GrantAdvertServiceTest {

    @Mock
    private GrantAdvertRepository grantAdvertRepository;
    @Mock
    private GrantApplicationService grantApplicationService;

    @Mock
    private CDAClient contentfulDeliveryClient;

    @InjectMocks
    private GrantAdvertService grantAdvertService;

    private final UUID ADVERT_ID = UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c");

    @Mock
    private CDAArray contentfulResults;

    @Mock
    private AbsQuery query;

    @Mock
    private FetchQuery fetchQuery;


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

    @Test
    void advertExistsInContentful_HandlesCDAResourceNotFoundException() {
        final String advertSlug = "chargepoint-grant-for-homeowners-1";

        when(contentfulDeliveryClient.fetch(CDAEntry.class))
                .thenThrow(CDAResourceNotFoundException.class);

        final boolean methodResponse = grantAdvertService.advertExistsInContentful(advertSlug);

        assertThat(methodResponse).isFalse();
    }

    @Test
    void advertExistsInContentful_ThrowsAnyOtherKindOfException() {
        final String advertSlug = "chargepoint-grant-for-homeowners-1";

        when(contentfulDeliveryClient.fetch(CDAEntry.class))
                .thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> grantAdvertService.advertExistsInContentful(advertSlug));
    }

    @Test
    void advertExistsInContentful_ReturnsTrue_IfResultsFoundInContentful() {
        final String advertSlug = "chargepoint-grant-for-homeowners-1";

        final CDAEntry entry = new CDAEntry();

        final Map<String, CDAEntry> entries = new HashMap<>();
        entries.put("entry", entry);

        when(contentfulDeliveryClient.fetch(CDAEntry.class))
                .thenReturn(fetchQuery);

        when(fetchQuery.withContentType("grantDetails"))
                .thenReturn(fetchQuery);

        when(fetchQuery.where("fields.label", advertSlug))
                .thenReturn(fetchQuery);

        when(fetchQuery.all())
                .thenReturn(contentfulResults);

        when(contentfulResults.entries())
                .thenReturn(entries);

        final boolean methodResponse = grantAdvertService.advertExistsInContentful(advertSlug);

        assertThat(methodResponse).isTrue();
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