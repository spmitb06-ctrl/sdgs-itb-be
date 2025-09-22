package com.sdgs.itb.entity.news;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sdgs.itb.entity.sdgs.Goal;
import com.sdgs.itb.entity.sdgs.Scholar;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "articles", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_id_gen")
    @SequenceGenerator(name = "article_id_gen", sequenceName = "article_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 250)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "source_url")
    private String sourceUrl;

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

    // ========================
    // Relationships
    // ========================

    @JsonBackReference
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleFaculty> articleFaculties = new HashSet<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ArticleImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleGoal> articleGoals = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_category_id")
    private ArticleCategory articleCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scholar_id")
    private Scholar scholar;

    // ========================
    // Helper methods
    // ========================

    @Transient
    public Set<Goal> getGoals() {
        Set<Goal> goals = new HashSet<>();
        for (ArticleGoal articleGoal : articleGoals) {
            goals.add(articleGoal.getGoal());
        }
        return goals;
    }

    public void addGoal(Goal goal) {
        // rely on Set + equals/hashCode in ArticleGoal to prevent duplicates
        ArticleGoal articleGoal = new ArticleGoal();
        articleGoal.setArticle(this);
        articleGoal.setGoal(goal);

        articleGoals.add(articleGoal);   // HashSet prevents duplicates
    }

    public void removeGoal(Goal goal) {
        articleGoals.removeIf(ag -> ag.getGoal().equals(goal));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return id != null && id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
