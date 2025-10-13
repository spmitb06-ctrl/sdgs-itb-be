package com.sdgs.itb.entity.news;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.entity.unit.Unit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "news", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_id_gen")
    @SequenceGenerator(name = "news_id_gen", sequenceName = "news_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "scholar_year")
    private String scholarYear;

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

    @JsonBackReference
    @OneToMany(mappedBy = "news", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NewsUnit> newsUnits = new HashSet<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NewsImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "news", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NewsGoal> newsGoals = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_category_id")
    private NewsCategory newsCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scholar_id")
    private Scholar scholar;

    // Helper methods

    @Transient
    public Set<Goal> getGoals() {
        Set<Goal> goals = new HashSet<>();
        for (NewsGoal newsGoal : newsGoals) {
            goals.add(newsGoal.getGoal());
        }
        return goals;
    }

    public void addGoal(Goal goal) {
        boolean exists = newsGoals.stream()
                .anyMatch(ng -> ng.getGoal() != null && ng.getGoal().equals(goal));
        if (!exists) {
            NewsGoal newsGoal = new NewsGoal();
            newsGoal.setNews(this);
            newsGoal.setGoal(goal);
            newsGoals.add(newsGoal);
        }
    }

    public void addUnit(Unit unit) {
        boolean exists = newsUnits.stream()
                .anyMatch(nu -> nu.getUnit() != null && nu.getUnit().equals(unit));
        if (!exists) {
            NewsUnit newsUnit = new NewsUnit();
            newsUnit.setNews(this);
            newsUnit.setUnit(unit);
            newsUnits.add(newsUnit);
        }
    }

    public void setUnits(Set<Unit> newUnits) {
        // Remove missing units
        this.newsUnits.removeIf(newsUnit ->
                !newUnits.contains(newsUnit.getUnit())
        );

        // Add new units
        for (Unit unit : newUnits) {
            boolean exists = this.newsUnits.stream()
                    .anyMatch(nu -> nu.getUnit().equals(unit));
            if (!exists) {
                NewsUnit nu = new NewsUnit();
                nu.setNews(this);
                nu.setUnit(unit);
                this.newsUnits.add(nu);
            }
        }
    }

    public void setGoals(Set<Goal> newGoals) {
        this.newsGoals.removeIf(newsGoal ->
                !newGoals.contains(newsGoal.getGoal())
        );

        for (Goal goal : newGoals) {
            boolean exists = this.newsGoals.stream()
                    .anyMatch(ng -> ng.getGoal().equals(goal));
            if (!exists) {
                NewsGoal ng = new NewsGoal();
                ng.setNews(this);
                ng.setGoal(goal);
                this.newsGoals.add(ng);
            }
        }
    }

    public void removeGoal(Goal goal) {
        newsGoals.removeIf(ag -> ag.getGoal().equals(goal));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News news)) return false;
        return id != null && id.equals(news.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
