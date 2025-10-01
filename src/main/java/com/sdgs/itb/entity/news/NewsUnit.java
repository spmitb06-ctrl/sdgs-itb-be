package com.sdgs.itb.entity.news;

import com.sdgs.itb.entity.unit.Unit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "news_units", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_unit_id_gen")
    @SequenceGenerator(name = "news_unit_id_gen", sequenceName = "news_unit_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

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

    // Relationships
    @ManyToOne
    @JoinColumn(name = "news_id")
    private News news;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsUnit that)) return false;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return news != null && news != null &&
                Objects.equals(news.getId(), that.news.getId()) &&
                Objects.equals(news.getId(), that.news.getId());
    }

    @Override
    public int hashCode() {
        return id != null
                ? id.hashCode()
                : Objects.hash(
                news != null ? news.getId() : 0,
                news != null ? news.getId() : 0
        );
    }

}


