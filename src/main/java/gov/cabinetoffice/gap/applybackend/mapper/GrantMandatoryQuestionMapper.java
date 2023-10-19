package gov.cabinetoffice.gap.applybackend.mapper;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantMandatoryQuestionDto;
import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantOrganisationType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.model.GrantMandatoryQuestions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;


@Mapper(componentModel = "spring")
public interface GrantMandatoryQuestionMapper {


    @Mapping(source = "orgType", target = "orgType", qualifiedByName = "mapEntityOrgTypeToDtoOrgType")
    @Mapping(source = "fundingLocation", target = "fundingLocation", qualifiedByName = "mapEntityFundingLocationToDtoFundingLocation")
    GetGrantMandatoryQuestionDto mapGrantMandatoryQuestionToGetGrantMandatoryQuestionDTO(GrantMandatoryQuestions source);

    @Named("mapEntityOrgTypeToDtoOrgType")
    default String mapEntityOrgTypeToDtoOrgType(GrantMandatoryQuestionOrgType type) {
        return type.name();
    }

    @Named("mapEntityFundingLocationToDtoFundingLocation")
    default List<String> mapEntityFundingLocationToDtoFundingLocation(GrantMandatoryQuestionFundingLocation[] type) {
        List<String> fundingLocation = new ArrayList<>();
       if(type != null){
              for (GrantMandatoryQuestionFundingLocation fundingLocationType : type) {
                fundingLocation.add(fundingLocationType.name());
              }
       }
        return fundingLocation;
    }

}

