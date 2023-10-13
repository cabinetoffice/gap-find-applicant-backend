package gov.cabinetoffice.gap.applybackend.service;


import gov.cabinetoffice.gap.applybackend.dto.api.GetGrandAdvertDto;
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

        final Optional<GrantAdvert> advert = grantAdvertRepository.findByContentfulSlug(contentfulSlug);
        if(advert.isEmpty()){
            log.debug("Advert with slug {} not found", contentfulSlug);
            return GetGrandAdvertDto.builder()
                    .isAdvertOnlyInContentful(true)
                    .build();
        }
        log.debug("Advert with slug {} found", contentfulSlug);
        final boolean isInternal = grantApplicationService.doesSchemeHaveApplication(advert.get().getScheme());
        final Integer grantApplicationId = grantApplicationService.getGrantApplicationId(advert.get().getScheme());
        return GetGrandAdvertDto.builder()
                .id(advert.get().getId())
                .version(advert.get().getVersion())
                .externalSubmissionUrl(getExternalSubmissionUrl(advert.get()))
                .isInternal(isInternal)
                .grantApplicationId(grantApplicationId)
                .grantSchemeId(advert.get().getScheme().getId())
                .isAdvertOnlyInContentful(false)
                .build();
    }

}
