package gov.cabinetoffice.gap.applybackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "grant_scheme")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrantScheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grant_scheme_id")
    private Integer id;

    @Column(name = "funder_id", nullable = false)
    private Integer funderId;

    @Builder.Default
    @Column(name = "version", nullable = false)
    private Integer version = 1;
    
    @Builder.Default
    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @Column(name = "last_updated_by")
    private Integer lastUpdatedBy;

    @Column(name = "ggis_identifier", nullable = false)
    private String ggisIdentifier;

    @Column(name = "scheme_name", nullable = false)
    private String name;

    @Column(name = "scheme_contact")
    private String email;
}
