package gov.cabinetoffice.gap.applybackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "spotlight_batch")
public class SpotlightBatch {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private String status;

    @Column
    private Instant lastSendAttempt;

    @Column
    private int version;

    @Column(name = "created", nullable = false)
    @Builder.Default
    private Instant created = Instant.now();

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @ManyToMany
    @JoinTable(
            name = "spotlight_batch_submission",
            joinColumns = @JoinColumn(name = "spotlight_batch_id"),
            inverseJoinColumns = @JoinColumn(name = "spotlight_submission_id"))
    private List<SpotlightSubmission> spotlightSubmissions;
}
