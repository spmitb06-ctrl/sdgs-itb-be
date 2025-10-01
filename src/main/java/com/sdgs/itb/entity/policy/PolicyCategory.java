package com.sdgs.itb.entity.policy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "policy_categories", schema = "sdgs")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policy_category_id_gen")
    @SequenceGenerator(name = "policy_category_id_gen", sequenceName = "policy_category_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @NotNull
    @Column
    private String category;

    @Column
    private String color;

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

    // Relationships
    @OneToMany(mappedBy = "policyCategory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Policy> policies = new ArrayList<>();
}

