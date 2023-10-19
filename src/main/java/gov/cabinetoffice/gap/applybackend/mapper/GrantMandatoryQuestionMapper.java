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


@Mapper(componentModel = "spring")
public interface GrantMandatoryQuestionMapper {


    @Mapping(source = "orgType", target = "orgType", qualifiedByName = "mapEntityOrgTypeToDtoOrgType")
    @Mapping(source = "fundingLocation", target = "fundingLocation", qualifiedByName = "mapEntityFundingLocationToDtoFundingLocation")
    GetGrantMandatoryQuestionDto mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(GrantMandatoryQuestions source);

    @Named("mapEntityOrgTypeToDtoOrgType")
    default String mapEntityOrgTypeToDtoOrgType(GrantMandatoryQuestionOrgType type) {
        if (type != null) {
            return type.name();
        }
        return null;
    }

    @Named("mapEntityFundingLocationToDtoFundingLocation")
    default List<String> mapEntityFundingLocationToDtoFundingLocation(GrantMandatoryQuestionFundingLocation[] type) {
        List<String> fundingLocation = new ArrayList<>();
        if (type != null) {
            for (GrantMandatoryQuestionFundingLocation fundingLocationType : type) {
                fundingLocation.add(fundingLocationType.name());
            }
        }
        return fundingLocation;
    }

    //nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    //it will copy the value from the dto to the entity, only if the Dto property is not null
    @Mapping(source = "name", target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "addressLine1", target = "addressLine1", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "addressLine2", target = "addressLine2", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "county", target = "county", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "city", target = "city", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "postcode", target = "postcode", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "charityCommissionNumber", target = "charityCommissionNumber", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "companiesHouseNumber", target = "companiesHouseNumber", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "orgType", target = "orgType", qualifiedByName = "mapDtoOrgTypeToEntityOrgType", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "fundingAmount", target = "fundingAmount", qualifiedByName = "mapDtoFundingAmountToEntityFundingAmount", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "fundingLocation", target = "fundingLocation", qualifiedByName = "mapDtoFundingLocationToEntityFundingLocation", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GrantMandatoryQuestions mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(UpdateGrantMandatoryQuestionDto dto, @MappingTarget GrantMandatoryQuestions entity);

    @Named("mapDtoOrgTypeToEntityOrgType")
    default GrantMandatoryQuestionOrgType mapDtoOrgTypeToEntityOrgType(String orgType) {
        if (orgType != null) {
            return GrantMandatoryQuestionOrgType.valueOfName(orgType);
        }
        return null;
    }

    @Named("mapDtoFundingAmountToEntityFundingAmount")
    default BigDecimal mapDtoFundingAmountToEntityFundingAmount(String fundingAmount) {
        if (fundingAmount != null) {
            return new BigDecimal(fundingAmount);
        }
        return null;
    }

    @Named("mapDtoFundingLocationToEntityFundingLocation")
    default GrantMandatoryQuestionFundingLocation[] mapDtoFundingLocationToEntityFundingLocation(List<String> fundingLocations) {

        if (fundingLocations != null) {
            GrantMandatoryQuestionFundingLocation[] grantMandatoryQuestionFundingLocations = new GrantMandatoryQuestionFundingLocation[fundingLocations.size()];
            for (int i = 0; i < fundingLocations.size(); i++) {
                grantMandatoryQuestionFundingLocations[i] = GrantMandatoryQuestionFundingLocation.valueOfName(fundingLocations.get(i));
            }
            return grantMandatoryQuestionFundingLocations;
        }
        return null;
    }
}

