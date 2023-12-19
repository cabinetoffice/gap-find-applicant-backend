package gov.cabinetoffice.gap.applybackend.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "gap_user")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GapUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gap_user_id")
    private Integer id;

    @Column(name = "user_sub")
    private String userSub;

}
