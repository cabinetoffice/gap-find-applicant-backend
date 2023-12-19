package gov.cabinetoffice.gap.applybackend.mapper;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantOrganisationType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import org.mapstruct.*;


@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GrantApplicantOrganisationProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "legalName", target = "name")
    @Mapping(source = "town", target = "city")
    @Mapping(source = "type", target = "orgType", qualifiedByName = "mapTypeToOrgType")
    GrantMandatoryQuestions mapOrgProfileToGrantMandatoryQuestion(GrantApplicantOrganisationProfile source);

    @Named("mapTypeToOrgType")
    default GrantMandatoryQuestionOrgType mapTypeToOrgType(GrantApplicantOrganisationType type) {
        return GrantMandatoryQuestionOrgType.valueOf(type.name());
    }
}
