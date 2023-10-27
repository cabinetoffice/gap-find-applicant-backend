package gov.cabinetoffice.gap.applybackend.service;

import com.contentful.java.cda.AbsQuery;
import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResourceNotFoundException;
import com.contentful.java.cda.FetchQuery;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantAdvertDto;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvert;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertPageResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertQuestionResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertSectionResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.repository.GrantAdvertRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantAdvertServiceTest {

    private final UUID ADVERT_ID = UUID.fromString("75ab5fbd-0682-4d3d-a467-01c7a447f07c");
    @Mock
    private GrantAdvertRepository grantAdvertRepository;
    @Mock
    private GrantApplicationService grantApplicationService;
    @Mock
    private CDAClient contentfulDeliveryClient;
    @InjectMocks
    private GrantAdvertService grantAdvertService;
    @Mock
    private CDAArray contentfulResults;

    @Mock
    private AbsQuery query;

    @Mock
    private FetchQuery fetchQuery;

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

    @Nested
    class getExternalSubmissionUrl {
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

    }

    @Nested
    class getAdvertByContentfulSlug{
        @Test
        void getAdvertByContentfulSlug() {
            final String contentfulSlug = "slug";
            final GrantAdvert advert = GrantAdvert.builder()
                    .contentfulSlug(contentfulSlug)
                    .build();

            when(grantAdvertRepository.findByContentfulSlug(contentfulSlug)).thenReturn(Optional.of(advert));

            final GrantAdvert methodResponse = grantAdvertService.getAdvertByContentfulSlug(contentfulSlug);

            assertThat(methodResponse).isEqualTo(advert);
        }

        @Test
        void getAdvertByContentfulSlug_throwsNotFoundException() {
            final String contentfulSlug = "slug";

            when(grantAdvertRepository.findByContentfulSlug(contentfulSlug)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> grantAdvertService.getAdvertByContentfulSlug(contentfulSlug));
        }
    }
    @Nested
    class generateGetGrandAdvertDto {
        @Test
        void generateGetGrantAdvertDto_createDtoForInternalApplicationAndVersion1() {
            final GrantAdvertResponse response = generateResponseWithNoHowToApplySection();
            final GrantScheme scheme = GrantScheme.builder().id(1).build();
            final GrantAdvert advert = GrantAdvert.builder()
                    .contentfulSlug("slug")
                    .id(ADVERT_ID)
                    .version(1)
                    .scheme(scheme)
                    .response(response)
                    .build();

            when(grantApplicationService.doesSchemeHaveApplication(scheme)).thenReturn(true);
            when(grantApplicationService.getGrantApplicationId(scheme)).thenReturn(1);

            final GetGrantAdvertDto methodResponse = grantAdvertService.generateGetGrantAdvertDto(advert);

            assertThat(methodResponse.getId()).isEqualTo(ADVERT_ID);
            assertThat(methodResponse.getVersion()).isEqualTo(1);
            assertThat(methodResponse.getExternalSubmissionUrl()).isEmpty();
            assertThat(methodResponse.isInternal()).isTrue();
            assertThat(methodResponse.getGrantApplicationId()).isEqualTo(1);
            assertThat(methodResponse.getGrantSchemeId()).isEqualTo(1);
        }

        @Test
        void generateGetGrantAdvertDto_createDtoForInternalApplicationAndVersion2() {
            final GrantAdvertResponse response = generateResponseWithNoHowToApplySection();
            final GrantScheme scheme = GrantScheme.builder().id(1).build();
            final GrantAdvert advert = GrantAdvert.builder()
                    .contentfulSlug("slug")
                    .id(ADVERT_ID)
                    .version(2)
                    .scheme(scheme)
                    .response(response)
                    .build();

            when(grantApplicationService.doesSchemeHaveApplication(scheme)).thenReturn(true);
            when(grantApplicationService.getGrantApplicationId(scheme)).thenReturn(1);

            final GetGrantAdvertDto methodResponse = grantAdvertService.generateGetGrantAdvertDto(advert);

            assertThat(methodResponse.getId()).isEqualTo(ADVERT_ID);
            assertThat(methodResponse.getVersion()).isEqualTo(2);
            assertThat(methodResponse.getExternalSubmissionUrl()).isEmpty();
            assertThat(methodResponse.isInternal()).isTrue();
            assertThat(methodResponse.getGrantApplicationId()).isEqualTo(1);
            assertThat(methodResponse.getGrantSchemeId()).isEqualTo(1);
        }

        @Test
        void generateGetGrantAdvertDto_createDtoForExternalApplicationAndVersion2() {
            final GrantAdvertResponse response = genereteResponseWithHowToApplySection();
            final GrantScheme scheme = GrantScheme.builder().id(1).build();
            final GrantAdvert advert = GrantAdvert.builder()
                    .contentfulSlug("slug")
                    .id(ADVERT_ID)
                    .version(2)
                    .scheme(scheme)
                    .response(response)
                    .build();


            when(grantApplicationService.doesSchemeHaveApplication(scheme)).thenReturn(false);
            when(grantApplicationService.getGrantApplicationId(scheme)).thenReturn(null);

            final GetGrantAdvertDto methodResponse = grantAdvertService.generateGetGrantAdvertDto(advert);

            assertThat(methodResponse.getId()).isEqualTo(ADVERT_ID);
            assertThat(methodResponse.getVersion()).isEqualTo(2);
            assertThat(methodResponse.getExternalSubmissionUrl()).isEqualTo("responseUrl");
            assertThat(methodResponse.isInternal()).isFalse();
            assertThat(methodResponse.getGrantApplicationId()).isNull();
            assertThat(methodResponse.getGrantSchemeId()).isEqualTo(1);
        }

        @Test
        void generateGetGrantAdvertDto_createDtoForExternalApplicationAndVersion1() {
            final GrantAdvertResponse response = genereteResponseWithHowToApplySection();
            final GrantScheme scheme = GrantScheme.builder().id(1).build();
            final GrantAdvert advert = GrantAdvert.builder()
                    .contentfulSlug("slug")
                    .id(ADVERT_ID)
                    .version(1)
                    .scheme(scheme)
                    .response(response)
                    .build();


            when(grantApplicationService.doesSchemeHaveApplication(scheme)).thenReturn(false);
            when(grantApplicationService.getGrantApplicationId(scheme)).thenReturn(null);

            final GetGrantAdvertDto methodResponse = grantAdvertService.generateGetGrantAdvertDto(advert);

            assertThat(methodResponse.getId()).isEqualTo(ADVERT_ID);
            assertThat(methodResponse.getVersion()).isEqualTo(1);
            assertThat(methodResponse.getExternalSubmissionUrl()).isEqualTo("responseUrl");
            assertThat(methodResponse.isInternal()).isFalse();
            assertThat(methodResponse.getGrantApplicationId()).isNull();
            assertThat(methodResponse.getGrantSchemeId()).isEqualTo(1);
        }
    }

    @Nested
    class advertExistsInContentful {
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
    }

    @Nested
    class getAdvertBySchemeId{
        @Test
        void getAdvertBySchemeId() {
            final String schemeId = "1";
            final GrantAdvert advert = GrantAdvert.builder()
                    .scheme(GrantScheme.builder().id(1).build())
                    .build();

            when(grantAdvertRepository.findBySchemeId(Integer.parseInt(schemeId))).thenReturn(Optional.of(advert));

            final GrantAdvert methodResponse = grantAdvertService.getAdvertBySchemeId(schemeId);

            assertThat(methodResponse).isEqualTo(advert);
        }

        @Test
        void getAdvertBySchemeId_throwsNotFoundException() {
            final String schemeId = "1";

            when(grantAdvertRepository.findBySchemeId(Integer.parseInt(schemeId))).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> grantAdvertService.getAdvertBySchemeId(schemeId));
        }
    }
}