package com.sdgs.itb.entity.news;

import com.sdgs.itb.entity.sdgs.Goal;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "article_goals",
        schema = "sdgs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"article_id", "goal_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_goal_id_gen")
    @SequenceGenerator(
            name = "article_goal_id_gen",
            sequenceName = "article_goal_id_seq",
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
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleGoal that)) return false;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return article != null && goal != null &&
                Objects.equals(article.getId(), that.article.getId()) &&
                Objects.equals(goal.getId(), that.goal.getId());
    }

    @Override
    public int hashCode() {
        return id != null
                ? id.hashCode()
                : Objects.hash(
                article != null ? article.getId() : 0,
                goal != null ? goal.getId() : 0
        );
    }
}
