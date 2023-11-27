package gov.cabinetoffice.gap.applybackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grant_scheme")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrantScheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grant_scheme_id", nullable = false)
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

    @ToString.Exclude
    @OneToMany(mappedBy = "scheme", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<GrantAdvert> grantAdverts = new ArrayList<>();

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "grantScheme")
    @JsonBackReference
    private GrantApplication grantApplication;
}
