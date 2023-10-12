package gov.cabinetoffice.gap.applybackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantAdvertQuestionResponse {

    private String id;

    private Boolean seen;

    private String response;

    private String[] multiResponse;

}
