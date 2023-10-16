package gov.cabinetoffice.gap.applybackend.service;


import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResourceNotFoundException;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrandAdvertDto;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvert;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertQuestionResponse;
import gov.cabinetoffice.gap.applybackend.repository.GrantAdvertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrantAdvertService {

    private final GrantAdvertRepository grantAdvertRepository;

    private final GrantApplicationService grantApplicationService;
    private final CDAClient contentfulDeliveryClient;

    protected static String getExternalSubmissionUrl(GrantAdvert advert) {
        return advert.getResponse().getSections().stream()
                .filter(section -> section.getId().equals("howToApply"))
                .flatMap(section -> section.getPages().stream())
                .flatMap(page -> page.getQuestions().stream())
                .filter(question -> question.getId().equals("grantWebpageUrl"))
                .map(GrantAdvertQuestionResponse::getResponse)
                .findFirst().orElse("");
    }

    public GetGrandAdvertDto getAdvertByContentfulSlug(String contentfulSlug) {

        final GrantAdvert advert = grantAdvertRepository.findByContentfulSlug(contentfulSlug)
                .orElseThrow(() -> new NotFoundException("Advert with slug " + contentfulSlug + " not found"));
        log.debug("Advert with slug {} found", contentfulSlug);
        final boolean isInternal = grantApplicationService.doesSchemeHaveApplication(advert.getScheme());
        final Integer grantApplicationId = grantApplicationService.getGrantApplicationId(advert.getScheme());
        return GetGrandAdvertDto.builder()
                .id(advert.getId())
                .version(advert.getVersion())
                .externalSubmissionUrl(getExternalSubmissionUrl(advert))
                .isInternal(isInternal)
                .grantApplicationId(grantApplicationId)
                .grantSchemeId(advert.getScheme().getId())
                .build();
    }

    public boolean advertExistsInContentful(final String advertSlug) {
        boolean advertExists = false;

        try {
            final CDAArray array = contentfulDeliveryClient
                    .fetch(CDAEntry.class)
                    .withContentType("grantDetails")
                    .where("fields.label", advertSlug)
                    .all();

            advertExists = !array.entries().isEmpty();
        } catch (CDAResourceNotFoundException e) {
            log.info(String.format("Advert with slug %s not found in Contentful", advertSlug));
        }

        return advertExists;
    }
}
