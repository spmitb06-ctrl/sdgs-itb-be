package com.sdgs.itb.entity.ranking;

import com.sdgs.itb.entity.goal.Goal;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "ranking_overviews",
        schema = "sdgs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ranking_system_id", "goal_id", "year"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingOverview {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ranking_overview_id_gen")
    @SequenceGenerator(name = "ranking_overview_id_gen", sequenceName = "ranking_overview_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_system_id", nullable = false)
    private RankingSystem rankingSystem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "rank_position")
    private String rankPosition; // Misal: "Rank 30", "Top 100"

    private Double score;

    @Column(columnDefinition = "TEXT")
    private String content; // Konten ringkasan pencapaian yang rapi untuk ditampilkan pada halaman

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_published", nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (isPublished == null) {
            isPublished = false;
        }
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @PreRemove
    protected void onRemove() {
        deletedAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RankingOverview that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}