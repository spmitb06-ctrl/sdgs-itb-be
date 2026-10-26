package com.sdgs.itb.entity.ranking;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "ranking_systems", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ranking_system_id_gen")
    @SequenceGenerator(name = "ranking_system_id_gen", sequenceName = "ranking_system_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Contoh: Times Higher Education, QS World University, UI GreenMetric

    @Column(nullable = false, unique = true, length = 50)
    private String code; // THE, QS, UIGM

    @Column(columnDefinition = "TEXT")
    private String description;

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
        if (!(o instanceof RankingSystem that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}