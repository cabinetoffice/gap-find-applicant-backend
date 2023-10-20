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


@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GrantMandatoryQuestionMapper {


    @Mapping(source = "orgType", target = "orgType", qualifiedByName = "mapEntityOrgTypeToDtoOrgType")
    @Mapping(source = "fundingAmount", target = "fundingAmount", qualifiedByName = "mapEntityFundingAmountToDtoFundingAmount")
    @Mapping(source = "fundingLocation", target = "fundingLocation", qualifiedByName = "mapEntityFundingLocationToDtoFundingLocation")
    GetGrantMandatoryQuestionDto mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(GrantMandatoryQuestions source);

    @Named("mapEntityOrgTypeToDtoOrgType")
    default String mapEntityOrgTypeToDtoOrgType(GrantMandatoryQuestionOrgType type) {
        return type.name();
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
            fundingLocation.add(fundingLocationType.name());
        }
        return fundingLocation;
    }

    @Mapping(source = "orgType", target = "orgType", qualifiedByName = "mapDtoOrgTypeToEntityOrgType")
    @Mapping(source = "fundingAmount", target = "fundingAmount", qualifiedByName = "mapDtoFundingAmountToEntityFundingAmount")
    @Mapping(source = "fundingLocation", target = "fundingLocation", qualifiedByName = "mapDtoFundingLocationToEntityFundingLocation")
    GrantMandatoryQuestions mapUpdateGrantMandatoryQuestionDtoToGrantMandatoryQuestion(UpdateGrantMandatoryQuestionDto dto, @MappingTarget GrantMandatoryQuestions entity);

    @Named("mapDtoOrgTypeToEntityOrgType")
    default GrantMandatoryQuestionOrgType mapDtoOrgTypeToEntityOrgType(String orgType) {
        return GrantMandatoryQuestionOrgType.valueOfName(orgType);
    }

    @Named("mapDtoFundingAmountToEntityFundingAmount")
    default BigDecimal mapDtoFundingAmountToEntityFundingAmount(String fundingAmount) {
        return new BigDecimal(fundingAmount);
    }

    @Named("mapDtoFundingLocationToEntityFundingLocation")
    default GrantMandatoryQuestionFundingLocation[] mapDtoFundingLocationToEntityFundingLocation(List<String> fundingLocations) {
        if (fundingLocations == null) {
            return null;
        }
        final GrantMandatoryQuestionFundingLocation[] grantMandatoryQuestionFundingLocations = new GrantMandatoryQuestionFundingLocation[fundingLocations.size()];
        for (int i = 0; i < fundingLocations.size(); i++) {
            grantMandatoryQuestionFundingLocations[i] = GrantMandatoryQuestionFundingLocation.valueOfName(fundingLocations.get(i));
        }
        return grantMandatoryQuestionFundingLocations;
    }
}

