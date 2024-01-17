package gov.cabinetoffice.gap.applybackend.model;

import gov.cabinetoffice.gap.applybackend.enums.SpotlightSubmissionStatus;
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
@Table(name = "spotlight_submission")
public class SpotlightSubmission {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "grant_mandatory_questions_id")
    private GrantMandatoryQuestions mandatoryQuestions;

    @ManyToOne
    @JoinColumn(name = "grant_scheme")
    private GrantScheme grantScheme;

    @Builder.Default
    @Column
    private String status = SpotlightSubmissionStatus.QUEUED.toString();

    @Column(name = "last_send_attempt")
    private Instant lastSendAttempt;

    @Column
    private int version;

    @Column(name = "created", nullable = false)
    @Builder.Default
    private Instant created = Instant.now();

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @ManyToMany
    private List<SpotlightBatch> batches;
}
