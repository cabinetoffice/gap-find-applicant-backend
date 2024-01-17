package gov.cabinetoffice.gap.applybackend.service;


import com.contentful.java.cda.*;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantAdvertDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantAdvertStatus;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.mapper.GrantMandatoryQuestionMapper;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvert;
import gov.cabinetoffice.gap.applybackend.model.GrantAdvertQuestionResponse;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicant;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import gov.cabinetoffice.gap.applybackend.repository.GrantAdvertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrantAdvertService {

    private final GrantAdvertRepository grantAdvertRepository;
    private final GrantMandatoryQuestionMapper grantMandatoryQuestionMapper;
    private final GrantApplicationService grantApplicationService;
    private final CDAClient contentfulDeliveryClient;
    private final GrantMandatoryQuestionService grantMandatoryQuestionService;
    private final GrantApplicantService grantApplicantService;

    protected static String getExternalSubmissionUrl(GrantAdvert advert) {
        return advert.getResponse().getSections().stream()
                .filter(section -> section.getId().equals("howToApply"))
                .flatMap(section -> section.getPages().stream())
                .flatMap(page -> page.getQuestions().stream())
                .filter(question -> question.getId().equals("grantWebpageUrl"))
                .map(GrantAdvertQuestionResponse::getResponse)
                .findFirst().orElse("");
    }

    public GrantAdvert getAdvertByContentfulSlug(String contentfulSlug) {
        final GrantAdvert advert = grantAdvertRepository.findByContentfulSlug(contentfulSlug)
                .orElseThrow(() -> new NotFoundException("Advert with slug " + contentfulSlug + " not found"));

        log.debug("Advert with slug {} found", contentfulSlug);

        return advert;
    }

    public GetGrantAdvertDto generateGetGrantAdvertDto(GrantAdvert advert, GetGrantMandatoryQuestionDto mandatoryQuestions) {
        final boolean isInternal = grantApplicationService.doesSchemeHaveApplication(advert.getScheme());
        final Integer grantApplicationId = grantApplicationService.getGrantApplicationId(advert.getScheme());

        return GetGrantAdvertDto.builder()
                .id(advert.getId())
                .version(advert.getVersion())
                .externalSubmissionUrl(getExternalSubmissionUrl(advert))
                .isInternal(isInternal)
                .grantApplicationId(grantApplicationId)
                .grantSchemeId(advert.getScheme().getId())
                .isAdvertInDatabase(true)
                .mandatoryQuestionsDto(mandatoryQuestions)
                .isPublished(advert.getStatus() == GrantAdvertStatus.PUBLISHED)
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

    private String getGrantWebpageUrl(final CDAArray contentfulEntry){
        CDAEntry entry = ((CDAEntry) contentfulEntry.items().get(0));
        Map<String, Object> rawFields = entry.rawFields();
        Optional<String> optionalUrl = ((Map<String, String>) rawFields.get("grantWebpageUrl")).values().stream().findFirst();
        if(optionalUrl.isEmpty()){
            throw new NotFoundException("Grant webpage url not found");
        }
        return optionalUrl.get();
    }

    public void validateGrantWebpageUrl(final String contentfulSlug, final String grantWebpageUrl) {
        try {
            final CDAArray contentfulEntry = contentfulDeliveryClient
                    .fetch(CDAEntry.class)
                    .withContentType("grantDetails")
                    .where("fields.label", contentfulSlug).all();

            String url = this.getGrantWebpageUrl(contentfulEntry);

            if (!url.equals(grantWebpageUrl)) {
                log.error("Grant webpage url does not match the url in contentful - expected: " + url + " but received: " + grantWebpageUrl);
                throw new NotFoundException("Grant webpage url does not match the url in contentful");
            }
        } catch (CDAResourceNotFoundException error) {
            log.error(String.format("Advert with slug %s not found in Contentful", contentfulSlug));
            throw error;
        }
    }


    public GrantAdvert getAdvertBySchemeId(String schemeId) {
        final GrantAdvert grantAdvert = grantAdvertRepository.findBySchemeId(Integer.parseInt(schemeId))
                .orElseThrow(() -> new NotFoundException("Advert with schemeId " + schemeId + " not found"));
        log.debug("Advert with schemeId {} found", schemeId);
        return grantAdvert;
    }

    public GetGrantAdvertDto grantAdvertToDto(
            final GrantAdvert grantAdvert,
            final String sub,
            final Integer schemeId
    ) {
        final GrantApplicant grantApplicant = grantApplicantService.getApplicantById(sub);
        GetGrantMandatoryQuestionDto mandatoryQuestionsDto = null;

        if (grantMandatoryQuestionService.mandatoryQuestionExistsBySchemeIdAndApplicantId(schemeId, grantApplicant.getId())) {
            final GrantMandatoryQuestions grantMandatoryQuestions = grantMandatoryQuestionService
                    .getMandatoryQuestionBySchemeId(schemeId, sub);
            mandatoryQuestionsDto = grantMandatoryQuestionMapper.mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(grantMandatoryQuestions);
        }

        return generateGetGrantAdvertDto(grantAdvert, mandatoryQuestionsDto);
    }
}
