package com.sdgs.itb.entity.sdgs;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sdgs.itb.entity.news.Article;
import com.sdgs.itb.entity.news.ArticleGoal;
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

    @Column(nullable = false, unique = true)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String color;
    private String icon;
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
    private Set<ArticleGoal> articleGoals = new HashSet<>();

    @JsonBackReference
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GoalScholar> goalScholars = new HashSet<>();

    @JsonIgnore
    @Transient
    public Set<Article> getArticles() {
        Set<Article> articles = new HashSet<>();
        for (ArticleGoal articleGoal : articleGoals) {
            articles.add(articleGoal.getArticle());
        }
        return articles;
    }

    public void addArticle(Article article) {
        boolean alreadyAdded = articleGoals.stream()
                .anyMatch(ag -> ag.getArticle().getId() != null && ag.getArticle().getId().equals(article.getId()));

        if (!alreadyAdded) {
            ArticleGoal articleGoal = new ArticleGoal();
            articleGoal.setGoal(this);
            articleGoal.setArticle(article);

            articleGoals.add(articleGoal);
            article.getArticleGoals().add(articleGoal);
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



