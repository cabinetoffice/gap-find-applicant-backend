package gov.cabinetoffice.gap.applybackend.model;

import gov.cabinetoffice.gap.applybackend.enums.GrantApplicantOrganisationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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

    public Boolean isComplete() {
        if (StringUtils.isEmpty(legalName)) return false;
        if (StringUtils.isEmpty(addressLine1)) return false;
        if (StringUtils.isEmpty(town)) return false;
        if (StringUtils.isEmpty(postcode)) return false;
        if (type == null) return false;
        if (type != GrantApplicantOrganisationType.INDIVIDUAL && type != GrantApplicantOrganisationType.NON_LIMITED_COMPANY) {
            if (StringUtils.isEmpty(getCompaniesHouseNumber())) return false;
            return !StringUtils.isEmpty(getCharityCommissionNumber());
        }
        return true;
    }
}
