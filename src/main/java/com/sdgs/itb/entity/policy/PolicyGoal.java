package com.sdgs.itb.entity.policy;

import com.sdgs.itb.entity.goal.Goal;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "policy_goals",
        schema = "sdgs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"policy_id", "goal_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policy_goal_id_gen")
    @SequenceGenerator(
            name = "policy_goal_id_gen",
            sequenceName = "policy_goal_id_seq",
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
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyGoal that)) return false;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return Objects.equals(policy != null ? policy.getId() : null,
                that.policy != null ? that.policy.getId() : null) &&
                Objects.equals(goal != null ? goal.getId() : null,
                        that.goal != null ? that.goal.getId() : null);
    }

    @Override
    public int hashCode() {
        return id != null
                ? id.hashCode()
                : Objects.hash(
                policy != null ? policy.getId() : null,
                goal != null ? goal.getId() : null
        );
    }
}
