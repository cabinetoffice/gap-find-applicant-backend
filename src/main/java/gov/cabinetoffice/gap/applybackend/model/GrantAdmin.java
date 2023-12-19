package gov.cabinetoffice.gap.applybackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "grant_admin")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrantAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grant_admin_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funder_id", referencedColumnName = "funder_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private FundingOrganisation funder;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "gap_user_id")
    private GapUser gapUser;

}
