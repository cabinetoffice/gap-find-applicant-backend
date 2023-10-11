package gov.cabinetoffice.gap.applybackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionStatus;
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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Table(name= "grant_mandatory_questions")
public class GrantMandatoryQuestions extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "grant_scheme_id")
    private GrantScheme grantScheme;

    @ManyToOne
    @JoinColumn(name = "submission_id", referencedColumnName = "id")
    private Submission submission;

    @Column(name = "name")
    private String name;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "county")
    private String county;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "org_type")
    private String orgType;

    @Column(name = "companies_house_number")
    private String companiesHouseNumber;

    @Column(name = "charity_commission_number")
    private String charityCommissionNumber;

    @Column(name = "funding_amount")
    private String fundingAmount;

    @Column(name = "funding_location")
    private String fundingLocation;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GrantMandatoryQuestionStatus status = GrantMandatoryQuestionStatus.NOT_STARTED;

    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    @Column(name = "created", nullable = false)
    @Builder.Default
    private Instant created = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private GrantApplicant createdBy;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by", referencedColumnName = "id")
    private GrantApplicant lastUpdatedBy;


}
