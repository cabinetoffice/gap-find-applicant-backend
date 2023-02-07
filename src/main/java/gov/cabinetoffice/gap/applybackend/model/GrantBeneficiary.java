package gov.cabinetoffice.gap.applybackend.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class GrantBeneficiary {

    @Id
    @GeneratedValue
    private UUID grantBeneficiaryId;

    @Column
    private Integer schemeId;

    @Column
    private Integer applicationId;

    @Column
    private UUID submissionId;

    @Column
    private long version;

    @CreatedDate
    private LocalDateTime created;

    @Column
    private long createdBy;

    @CreatedDate
    private LocalDateTime lastUpdated;

    @Column
    private long lastUpdatedBy;

    @Column
    private Boolean locationNeEng;

    @Column
    private Boolean locationNwEng;

    @Column
    private Boolean locationSeEng;

    @Column
    private Boolean locationSwEng;

    @Column
    private Boolean locationMidEng;

    @Column
    private Boolean locationSco;

    @Column
    private Boolean locationWal;

    @Column
    private Boolean locationNir;

    @Column
    private Boolean hasProvidedAdditionalAnswers;

    @Column
    private Boolean ageGroup1;

    @Column
    private Boolean ageGroup2;

    @Column
    private Boolean ageGroup3;

    @Column
    private Boolean ageGroup4;

    @Column
    private Boolean ageGroup5;

    @Column
    private Boolean ageGroupAll;

    @Column
    private Boolean ethnicGroup1;

    @Column
    private Boolean ethnicGroup2;

    @Column
    private Boolean ethnicGroup3;

    @Column
    private Boolean ethnicGroup4;

    @Column
    private Boolean ethnicGroup5;

    @Column
    private Boolean ethnicGroupOther;

    @Column(length = 100)
    private String ethnicOtherDetails;

    @Column
    private Boolean ethnicGroupAll;

    @Column
    private Boolean supportingDisabilities;

    @Column
    private Boolean sexualOrientationGroup1;

    @Column
    private Boolean sexualOrientationGroup2;

    @Column
    private Boolean sexualOrientationGroup3;

    @Column
    private Boolean sexualOrientationOther;

    @Column(length = 100)
    private String sexualOrientationOtherDetails;

    @Column
    private Boolean sexualOrientationGroupAll;

    @Column
    private Boolean sexGroup1;

    @Column
    private Boolean sexGroup2;

    @Column
    private Boolean sexGroupAll;

    @Column
    private String gapId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GrantBeneficiary that = (GrantBeneficiary) o;
        return grantBeneficiaryId != null && Objects.equals(grantBeneficiaryId, that.grantBeneficiaryId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}