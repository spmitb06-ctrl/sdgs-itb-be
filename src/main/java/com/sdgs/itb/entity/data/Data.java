package com.sdgs.itb.entity.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.entity.data.DataCategory;
import com.sdgs.itb.entity.data.DataGoal;
import com.sdgs.itb.entity.data.DataImage;
import com.sdgs.itb.entity.data.DataUnit;
import com.sdgs.itb.entity.unit.Unit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "data", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Data {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_id_gen")
    @SequenceGenerator(name = "data_id_gen", sequenceName = "data_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "event_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }
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
    @JoinColumn(name = "data_category_id")
    private DataCategory dataCategory;

    @JsonBackReference
    @OneToMany(mappedBy = "data", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DataUnit> dataUnits = new HashSet<>();

    @JsonBackReference
    @OneToMany(mappedBy = "data", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DataGoal> dataGoals = new HashSet<>();

    @JsonBackReference
    @OneToMany(mappedBy = "data", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DataImage> dataImages = new ArrayList<>();

    // Helper methods
    @Transient
    public Set<Goal> getGoals() {
        Set<Goal> goals = new HashSet<>();
        for (DataGoal dataGoal : dataGoals) {
            goals.add(dataGoal.getGoal());
        }
        return goals;
    }

    public void addGoal(Goal goal) {
        boolean exists = dataGoals.stream()
                .anyMatch(ng -> ng.getGoal() != null && ng.getGoal().equals(goal));
        if (!exists) {
            DataGoal dataGoal = new DataGoal();
            dataGoal.setData(this);
            dataGoal.setGoal(goal);
            dataGoals.add(dataGoal);
        }
    }

    public void addUnit(Unit unit) {
        boolean exists = dataUnits.stream()
                .anyMatch(nu -> nu.getUnit() != null && nu.getUnit().equals(unit));
        if (!exists) {
            DataUnit dataUnit = new DataUnit();
            dataUnit.setData(this);
            dataUnit.setUnit(unit);
            dataUnits.add(dataUnit);
        }
    }

    public void removeGoal(Goal goal) {
        dataGoals.removeIf(ag -> ag.getGoal().equals(goal));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data data)) return false;
        return id != null && id.equals(data.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
