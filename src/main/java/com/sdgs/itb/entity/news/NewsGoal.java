package com.sdgs.itb.entity.news;

import com.sdgs.itb.entity.goal.Goal;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "news_goals",
        schema = "sdgs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"news_id", "goal_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_goal_id_gen")
    @SequenceGenerator(
            name = "news_goal_id_gen",
            sequenceName = "news_goal_id_seq",
            schema = "sdgs",
            allocationSize = 1
    )
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsGoal that)) return false;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return news != null && goal != null &&
                Objects.equals(news.getId(), that.news.getId()) &&
                Objects.equals(goal.getId(), that.goal.getId());
    }

    @Override
    public int hashCode() {
        return id != null
                ? id.hashCode()
                : Objects.hash(
                news != null ? news.getId() : 0,
                goal != null ? goal.getId() : 0
        );
    }
}
