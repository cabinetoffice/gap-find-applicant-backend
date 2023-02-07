package gov.cabinetoffice.gap.applybackend.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionStatus;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionDefinition {
    @Builder.Default
    private List<SubmissionSection> sections = new ArrayList<>();

    public static SubmissionDefinition transformApplicationDefinitionToSubmissionOne(ApplicationDefinition applicationDefinition) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String applicationDefinitionJson = objectMapper.writeValueAsString(applicationDefinition);
        SubmissionDefinition definition = objectMapper.readValue(applicationDefinitionJson, SubmissionDefinition.class);
        definition.getSections().forEach(section -> {
                    final SubmissionSectionStatus status = section.getSectionId().equals("ELIGIBILITY") ? SubmissionSectionStatus.NOT_STARTED : SubmissionSectionStatus.CANNOT_START_YET;
                    section.setSectionStatus(status);
                }
        );
        return definition;
    }
}
