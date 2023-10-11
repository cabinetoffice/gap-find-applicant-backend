package gov.cabinetoffice.gap.applybackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class DiligenceCheck extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private UUID submissionId;

    @CreatedDate
    private LocalDateTime created;

    @Builder.Default
    @Column(length = 1)
    private int checkType = 1;

    @Column
    private String applicationNumber;

    @Column(length = 250)
    private String organisationName;

    @Column(length = 250)
    private String addressStreet;

    @Column(length = 250)
    private String addressTown;

    @Column(length = 250)
    private String addressCounty;

    @Column(length = 8)
    private String addressPostcode;

    @Column
    private String applicationAmount;

    @Column(length = 15)
    private String charityNumber;

    @Column(length = 8)
    private String companiesHouseNumber;
}
