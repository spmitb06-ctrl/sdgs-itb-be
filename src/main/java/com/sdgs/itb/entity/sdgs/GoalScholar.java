package com.sdgs.itb.entity.sdgs;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "goal_scholars", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalScholar {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "goal_scholar_id_gen")
    @SequenceGenerator(name = "goal_scholar_id_gen", sequenceName = "goal_scholar_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scholar_id", nullable = false)
    private Scholar scholar;

    @Column(name = "link")
    private String link;

    @Column(name = "count")
    private Integer count;
}
