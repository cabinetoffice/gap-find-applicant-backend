package gov.cabinetoffice.gap.applybackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantAdvertResponse {

    @Builder.Default
    private List<GrantAdvertSectionResponse> sections = new ArrayList<>();

    public Optional<GrantAdvertSectionResponse> getSectionById(String sectionId) {
        return sections.stream().filter(section -> Objects.equals(section.getId(), sectionId)).findFirst();
    }

    public String nullCheckSingleResponse(String sectionId, String pageId, String questionId) {

        Optional<GrantAdvertQuestionResponse> grantAdvertQuestionResponse = getSectionById(sectionId)
                .flatMap(section -> section.getPageById(pageId)).flatMap(page -> page.getQuestionById(questionId));

        return grantAdvertQuestionResponse.map(GrantAdvertQuestionResponse::getResponse).orElse("");

    }

    public String[] nullCheckMultiResponse(String sectionId, String pageId, String questionId) {

        Optional<GrantAdvertQuestionResponse> grantAdvertQuestionResponse = getSectionById(sectionId)
                .flatMap(section -> section.getPageById(pageId)).flatMap(page -> page.getQuestionById(questionId));

        return grantAdvertQuestionResponse.map(GrantAdvertQuestionResponse::getMultiResponse).orElse(null);

    }

}
