package gov.cabinetoffice.gap.applybackend.model;

import com.vladmihalcea.hibernate.type.array.EnumArrayType;
import com.vladmihalcea.hibernate.type.array.internal.AbstractArrayType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionFundingLocation;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionOrgType;
import gov.cabinetoffice.gap.applybackend.enums.GrantMandatoryQuestionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "grant_mandatory_questions")
@TypeDef(
        typeClass = EnumArrayType.class,
        defaultForType = GrantMandatoryQuestionFundingLocation[].class,
        parameters = {
                @Parameter(
                        name = AbstractArrayType.SQL_ARRAY_TYPE,
                        value = "grant_mandatory_question_funding_location"
                )
        }
)
public class GrantMandatoryQuestions extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "grant_scheme_id")
    private GrantScheme grantScheme;

    @OneToOne
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
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::grant_mandatory_question_type")
    private GrantMandatoryQuestionOrgType orgType;

    @Column(name = "companies_house_number")
    private String companiesHouseNumber;

    @Column(name = "charity_commission_number")
    private String charityCommissionNumber;

    @Column(name = "funding_amount", precision = 16, scale = 2) // this should match your database column definition
    private BigDecimal fundingAmount;

    @Column(name = "funding_location")
    private GrantMandatoryQuestionFundingLocation[] fundingLocation;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::grant_mandatory_question_status")
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
