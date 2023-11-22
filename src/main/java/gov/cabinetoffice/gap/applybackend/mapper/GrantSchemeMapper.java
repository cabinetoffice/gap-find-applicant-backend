package gov.cabinetoffice.gap.applybackend.mapper;

import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantApplicationDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantSchemeDto;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GrantSchemeMapper {
    GetGrantSchemeDto grantSchemeToGetGrantSchemeDto(GrantScheme grantScheme);

    @Mapping(source = "grantApplication", target = ".")
    GetGrantApplicationDto grantSchemeToGetGrantApplicationDto(GrantScheme grantScheme);
}
