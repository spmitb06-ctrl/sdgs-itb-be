package com.sdgs.itb.entity.goal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sdgs.itb.entity.news.News;
import com.sdgs.itb.entity.news.NewsGoal;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "goals", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "goal_id_gen")
    @SequenceGenerator(name = "goal_id_gen", sequenceName = "goal_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @Column(name = "goal_number", unique = true)
    private Integer goalNumber;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String color;
    private String icon;

    @Column(name = "edited_icon", unique = true)
    private String editedIcon;

    private String linkUrl;

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
    @OneToMany(mappedBy = "goal", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NewsGoal> newsGoals = new HashSet<>();

    @JsonBackReference
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GoalScholar> goalScholars = new HashSet<>();

    @JsonIgnore
    @Transient
    public Set<News> getNews() {
        Set<News> news = new HashSet<>();
        for (NewsGoal newsGoal : newsGoals) {
            news.add(newsGoal.getNews());
        }
        return news;
    }

    public void addNews(News news) {
        boolean alreadyAdded = newsGoals.stream()
                .anyMatch(ag -> ag.getNews().getId() != null && ag.getNews().getId().equals(news.getId()));

        if (!alreadyAdded) {
            NewsGoal newsGoal = new NewsGoal();
            newsGoal.setGoal(this);
            newsGoal.setNews(news);

            newsGoals.add(newsGoal);
            news.getNewsGoals().add(newsGoal);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Goal goal)) return false;
        return id != null && id.equals(goal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}



