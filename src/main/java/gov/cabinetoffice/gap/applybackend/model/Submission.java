package gov.cabinetoffice.gap.applybackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionStatus;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name= "grant_submission")
public class Submission extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    @JsonIgnoreProperties("submissions")
    private GrantApplicant applicant;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scheme_id")
    private GrantScheme scheme;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "application_id")
    @JsonIgnoreProperties("schemes")
    private GrantApplication application;

    @Column
    private int version;

    @CreatedDate
    private LocalDateTime created;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    @JsonIgnoreProperties("submissions")
    private GrantApplicant createdBy;

    @LastModifiedDate
    private LocalDateTime lastUpdated;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by", referencedColumnName = "id")
    @JsonIgnoreProperties("submissions")
    private GrantApplicant lastUpdatedBy;

    @Column
    private ZonedDateTime submittedDate;

    @Column
    private String applicationName;

    @Column
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private SubmissionDefinition definition;

    @Column
    private String gapId;

    @Column(name = "last_required_checks_export")
    private Instant lastRequiredChecksExport;

    public SubmissionSection getSection(String sectionId) {
        return this.getDefinition()
                .getSections()
                .stream().filter(section -> section.getSectionId().equals(sectionId))
                .findAny()
                .orElseThrow(() -> new NotFoundException(String.format("No Section with ID %s was found", sectionId)));
    }

    public SubmissionQuestion getQuestion(String sectionId, String questionId) {
        return this.getSection(sectionId)
                .getQuestions()
                .stream().filter(question -> question.getQuestionId().equals(questionId))
                .findAny()
                .orElseThrow(() -> new NotFoundException(String.format("No question with ID %s was found", questionId)));
    }

    public void removeSection(String sectionId) {
        this.getDefinition()
                .getSections()
                .removeIf(section -> section.getSectionId().equals(sectionId));
    }
}
