package gov.cabinetoffice.gap.applybackend.dto.api;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class GetSectionDto {

    private String sectionId;
    private String sectionTitle;
    private String sectionStatus;

    @Builder.Default
    private List<String> questionIds = new ArrayList<>();

    @Builder.Default
    private List<GetQuestionDto> questions = new ArrayList<>();
}
