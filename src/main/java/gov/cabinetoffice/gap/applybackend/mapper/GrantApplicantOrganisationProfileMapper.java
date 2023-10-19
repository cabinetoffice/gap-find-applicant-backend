package gov.cabinetoffice.gap.applybackend.mapper;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantOrganisationType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.model.GrantApplicantOrganisationProfile;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface GrantApplicantOrganisationProfileMapper {

        @Mapping(target = "id", ignore = true)
        @Mapping(source = "legalName", target = "name")
        @Mapping(source = "addressLine1", target = "addressLine1")
        @Mapping(source = "addressLine2", target = "addressLine2")
        @Mapping(source = "town", target = "city")
        @Mapping(source = "county", target = "county")
        @Mapping(source = "postcode", target = "postcode")
        @Mapping(source = "charityCommissionNumber", target = "charityCommissionNumber")
        @Mapping(source = "companiesHouseNumber", target = "companiesHouseNumber")
        @Mapping(source = "type", target = "orgType", qualifiedByName = "mapTypeToOrgType")
        GrantMandatoryQuestions mapOrgProfileToGrantMandatoryQuestion(GrantApplicantOrganisationProfile source);

    @Named("mapTypeToOrgType")
    default GrantMandatoryQuestionOrgType mapTypeToOrgType(GrantApplicantOrganisationType type) {
        return GrantMandatoryQuestionOrgType.valueOf(type.name());
    }
    }
