package gov.cabinetoffice.gap.applybackend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ApplicationFormQuestion {

    private String questionId;

    private String profileField;

    private String fieldPrefix;

    private String fieldTitle;

    private String hintText;

    private String adminSummary;

    private String displayText;

    private String questionSuffix;

    private String responseType;

    @Builder.Default
    private Map<String, Object> validation = new HashMap<>();

    private List<String> options;

}
