package com.sdgs.itb.entity.unit;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.news.News;
import com.sdgs.itb.entity.news.NewsUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "units", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unit_id_gen")
    @SequenceGenerator(name = "unit_id_gen", sequenceName = "unit_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String abbreviation;

    // --- New Columns ---
    @Column(name = "web")
    private String web;

    @Column(name = "slug")
    private String slug;

    @Column(name = "cite")
    private Integer cite;

    @Column(name = "paper")
    private Integer paper;

    @Column(name = "patent")
    private Integer patent;

    @Column(name = "project")
    private Integer project;

    @Column(name = "thesis")
    private Integer thesis;

    @Column(name = "outreach")
    private Integer outreach;

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
    @Builder.Default
    @JsonBackReference
    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NewsUnit> newsUnits = new HashSet<>();

    @Builder.Default
    @JsonBackReference
    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UnitGoal> unitGoals = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_type_id")
    private UnitType unitType;

    @JsonIgnore
    @Transient
    public Set<Goal> getGoals() {
        if (unitGoals == null) {
            return new HashSet<>();
        }
        Set<Goal> goals = new HashSet<>();
        for (UnitGoal unitGoal : unitGoals) {
            goals.add(unitGoal.getGoal());
        }
        return goals;
    }

    public void addGoal(Goal goal) {
        if (unitGoals == null) {
            unitGoals = new HashSet<>();
        }
        boolean exists = unitGoals.stream()
                .anyMatch(ug -> ug.getGoal() != null && ug.getGoal().equals(goal));
        if (!exists) {
            UnitGoal unitGoal = new UnitGoal();
            unitGoal.setUnit(this);
            unitGoal.setGoal(goal);
            unitGoals.add(unitGoal);
        }
    }

    @JsonIgnore
    @Transient
    public Set<News> getNews() {
        if (newsUnits == null) {
            return new HashSet<>();
        }
        Set<News> news = new HashSet<>();
        for (NewsUnit newsUnit : newsUnits) {
            news.add(newsUnit.getNews());
        }
        return news;
    }

    public void addNews(News news) {
        if (newsUnits == null) {
            newsUnits = new HashSet<>();
        }
        boolean alreadyAddedUnit = newsUnits.stream()
                .anyMatch(s -> {
                    News t = s.getNews();
                    if (t.getId() != null && news.getId() != null) {
                        return t.getId().equals(news.getId());
                    }
                    return t == news;
                });

        if (!alreadyAddedUnit) {
            NewsUnit newsUnit = new NewsUnit();
            newsUnit.setUnit(this);
            newsUnit.setNews(news);

            newsUnits.add(newsUnit);
            news.getNewsUnits().add(newsUnit);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit unit)) return false;
        return id != null && id.equals(unit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}