package gov.cabinetoffice.gap.applybackend.dto.api;

import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetQuestionDto {
  private Integer grantSchemeId;
  private Integer grantApplicationId;
  private UUID grantSubmissionId;
  private String sectionId;
  private String sectionTitle;
  private SubmissionQuestion question;
  private GetQuestionNavigationDto nextNavigation;
  private GetQuestionNavigationDto previousNavigation;
}


