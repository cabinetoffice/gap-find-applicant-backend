package gov.cabinetoffice.gap.applybackend.model;

import gov.cabinetoffice.gap.applybackend.enums.GrantAdvertPageResponseStatus;
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
public class GrantAdvertPageResponse {

    @Builder.Default
    private List<GrantAdvertQuestionResponse> questions = new ArrayList<>();

    private String id;

    private GrantAdvertPageResponseStatus status;

    public Optional<GrantAdvertQuestionResponse> getQuestionById(String questionId) {
        return questions.stream().filter(page -> Objects.equals(page.getId(), questionId)).findFirst();

    }

}
