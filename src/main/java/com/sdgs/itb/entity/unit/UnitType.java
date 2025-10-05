package com.sdgs.itb.entity.unit;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "unit_types", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unit_type_id_gen")
    @SequenceGenerator(name = "unit_type_id_gen", sequenceName = "unit_type_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

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
    @JsonBackReference
    @OneToMany(mappedBy = "unitType", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Unit> units = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
