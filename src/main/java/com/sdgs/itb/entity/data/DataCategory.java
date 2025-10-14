package com.sdgs.itb.entity.data;

import com.sdgs.itb.entity.data.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "data_categories", schema = "sdgs")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_category_id_gen")
    @SequenceGenerator(name = "data_category_id_gen", sequenceName = "data_category_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @NotNull
    @Column
    private String category;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

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
}

