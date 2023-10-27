package gov.cabinetoffice.gap.applybackend.mapper;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.dto.api.UpdateGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GrantMandatoryQuestionMapper {


    @Mapping(source= "grantScheme.id", target = "schemeId")
    @Mapping(source = "orgType", target = "orgType", qualifiedByName = "mapEntityOrgTypeToDtoOrgType")
    @Mapping(source = "fundingAmount", target = "fundingAmount", qualifiedByName = "mapEntityFundingAmountToDtoFundingAmount")
    @Mapping(source = "fundingLocation", target = "fundingLocation", qualifiedByName = "mapEntityFundingLocationToDtoFundingLocation")
    GetGrantMandatoryQuestionDto mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(GrantMandatoryQuestions source);

    @Named("mapEntityOrgTypeToDtoOrgType")
    default String mapEntityOrgTypeToDtoOrgType(GrantMandatoryQuestionOrgType type) {
        return type.toString();
    }

    @Named("mapEntityFundingAmountToDtoFundingAmount")
    default String mapEntityFundingAmountToDtoFundingAmount(BigDecimal fundingAmount) {
        return fundingAmount.toString();
    }

    @Named("mapEntityFundingLocationToDtoFundingLocation")
    default List<String> mapEntityFundingLocationToDtoFundingLocation(GrantMandatoryQuestionFundingLocation[] type) {
        if (type == null) {
            return null;
        }
        final List<String> fundingLocation = new ArrayList<>();
        for (GrantMandatoryQuestionFundingLocation fundingLocationType : type) {
            fundingLocation.add(fundingLocationType.getName());
        }
        return fundingLocation;
    }

    //at this time the Optional is still not fully implemented in MapStruct (there's a pr for it https://github.com/mapstruct/mapstruct/pull/3183), so we need to use the workaround below


    @Mapping(source = "name", target = "name", qualifiedByName = "unWrapOptionalString")
    @Mapping(source = "addressLine1", target = "addressLine1", qualifiedByName = "unWrapOptionalString")
    @Mapping(source = "addressLine2", target = "addressLine2", qualifiedByName = "unWrapOptionalString")
    @Mapping(source = "city", target = "city", qualifiedByName = "unWrapOptionalString")
    @Mapping(source = "county", target = "county", qualifiedByName = "unWrapOptionalString")
    @Mapping(source = "postcode", target = "postcode", qualifiedByName = "unWrapOptionalString")
    @Mapping(source = "orgType", target = "orgType", qualifiedByName = "mapDtoOrgTypeToEntityOrgType")
    @Mapping(source = "companiesHouseNumber", target = "companiesHouseNumber", qualifiedByName = "unWrapOptionalString")
    @Mapping(source = "charityCommissionNumber", target = "charityCommissionNumber", qualifiedByName = "unWrapOptionalString")
    @Mapping(source = "fundingAmount", target = "fundingAmount", qualifiedByName = "mapDtoFundingAmountToEntityFundingAmount")
    @Mapping(source = "fundingLocation", target = "fundingLocation", qualifiedByName = "mapDtoFundingLocationToEntityFundingLocation")
    GrantMandatoryQuestions mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(UpdateGrantMandatoryQuestionDto dto, @MappingTarget GrantMandatoryQuestions entity);

    @Named("unWrapOptionalString")
    default String unWrapOptionalString(Optional<String> string) {
        return string.orElse(null);
    }

    @Named("mapDtoOrgTypeToEntityOrgType")
    default GrantMandatoryQuestionOrgType mapDtoOrgTypeToEntityOrgType(Optional<String> orgType) {
        return GrantMandatoryQuestionOrgType.valueOfName(orgType.get());
    }

    @Named("mapDtoFundingAmountToEntityFundingAmount")
    default BigDecimal mapDtoFundingAmountToEntityFundingAmount(Optional<String> fundingAmount) {
        return new BigDecimal(fundingAmount.get());
    }

    @Named("mapDtoFundingLocationToEntityFundingLocation")
    default GrantMandatoryQuestionFundingLocation[] mapDtoFundingLocationToEntityFundingLocation(Optional<List<String>> fundingLocations) {
        if (fundingLocations.isEmpty()) {
            return null;
        }
        final GrantMandatoryQuestionFundingLocation[] grantMandatoryQuestionFundingLocations = new GrantMandatoryQuestionFundingLocation[fundingLocations.get().size()];
        for (int i = 0; i < fundingLocations.get().size(); i++) {
            grantMandatoryQuestionFundingLocations[i] = GrantMandatoryQuestionFundingLocation.valueOfName(fundingLocations.get().get(i));
        }
        return grantMandatoryQuestionFundingLocations;
    }
}

