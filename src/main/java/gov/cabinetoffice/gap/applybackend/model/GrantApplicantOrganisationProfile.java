package gov.cabinetoffice.gap.applybackend.model;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantOrganisationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class GrantApplicantOrganisationProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    private GrantApplicant applicant;

    @Column
    private String legalName;

    @Column
    @Enumerated(EnumType.STRING)
    private GrantApplicantOrganisationType type;

    @Column
    private String addressLine1;

    @Column
    private String addressLine2;

    @Column
    private String town;

    @Column
    private String county;

    @Column
    private String postcode;

    @Column
    private String charityCommissionNumber;

    @Column
    private String companiesHouseNumber;
}
