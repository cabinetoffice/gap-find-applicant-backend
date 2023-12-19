package gov.cabinetoffice.gap.applybackend.model;

import gov.cabinetoffice.gap.applybackend.enums.GrantAttachmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "grant_attachment")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrantAttachment {

    @Id
    @GeneratedValue
    @Column(name = "grant_attachment_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "submission_id", referencedColumnName = "id")
    private Submission submission;

    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    @Column(name = "created", nullable = false)
    @Builder.Default
    private Instant created = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private GrantApplicant createdBy;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GrantAttachmentStatus status;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "location", nullable = false, columnDefinition = "TEXT")
    private String location;
}
