package com.sdgs.itb.entity.data;

import com.sdgs.itb.entity.data.Data;
import com.sdgs.itb.entity.unit.Unit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "data_units",
        schema = "sdgs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"data_id", "unit_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_unit_id_gen")
    @SequenceGenerator(
            name = "data_unit_id_gen",
            sequenceName = "data_unit_id_seq",
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
    @JoinColumn(name = "data_id", nullable = false)
    private Data data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataUnit that)) return false;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return  Objects.equals(data != null ? data.getId() : null,
                        that.data != null ? that.data.getId() : null) &&
                Objects.equals(unit != null ? unit.getId() : null,
                        that.unit != null ? that.unit.getId() : null);
    }

    @Override
    public int hashCode() {
        return id != null
                ? id.hashCode()
                : Objects.hash(
                data != null ? data.getId() : null,
                unit != null ? unit.getId() : null
        );
    }
}
