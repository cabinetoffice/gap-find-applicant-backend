package gov.cabinetoffice.gap.applybackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import gov.cabinetoffice.gap.applybackend.enums.GrantApplicationStatus;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "grant_application")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrantApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grant_application_id")
    private Integer id;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "grant_scheme_id")
    @JsonManagedReference
    private GrantScheme grantScheme;

    @Column(name = "version")
    private Integer version;

    @Column(name = "created")
    private Instant created;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @Column(name = "last_update_by")
    private Integer lastUpdateBy;

    @Column(name = "application_name")
    private String applicationName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GrantApplicationStatus applicationStatus;

    @Column(name = "definition", nullable = false, columnDefinition = "json")
    @Type(type = "json")
    private ApplicationDefinition definition;

    public GrantApplication(GrantScheme grantScheme, String applicationName, Integer lastUpdateBy,
            ApplicationDefinition definition) {
        this.version = 2;
        this.created = Instant.now();
        this.lastUpdated = Instant.now();
        this.applicationStatus = GrantApplicationStatus.DRAFT;
        this.grantScheme = grantScheme;
        this.lastUpdateBy = lastUpdateBy;
        this.applicationName = applicationName;
        this.definition = definition;
    }

    public static GrantApplication createFromTemplate(GrantScheme grantScheme, String applicationName,
            Integer lastUpdateBy, ApplicationDefinition definition) {
        return new GrantApplication(grantScheme, applicationName, lastUpdateBy, definition);
    }
}
