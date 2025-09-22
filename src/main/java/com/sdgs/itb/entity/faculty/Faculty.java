package com.sdgs.itb.entity.faculty;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sdgs.itb.entity.news.Article;
import com.sdgs.itb.entity.news.ArticleFaculty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "faculties", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "faculty_id_gen")
    @SequenceGenerator(name = "faculty_id_gen", sequenceName = "faculty_id_seq", schema = "sdgs", allocationSize = 1)
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
    @OneToMany(mappedBy = "faculty", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleFaculty> articleFaculties = new HashSet<>();

    @JsonIgnore
    @Transient
    public Set<Article> getArticles() {
        Set<Article> articles = new HashSet<>();
        for (ArticleFaculty articleFaculty : articleFaculties) {
            articles.add(articleFaculty.getArticle());
        }
        return articles;
    }

    public void addArticle(Article article) {
        boolean alreadyAddedFaculty = articleFaculties.stream()
                .anyMatch(s -> {
                    Article t = s.getArticle();

                    // ✅ If both IDs exist, compare by ID
                    if (t.getId() != null && article.getId() != null) {
                        return t.getId().equals(article.getId());
                    }

                    // ✅ If IDs are not set yet, compare by object reference
                    return t == article;
                });

        if (!alreadyAddedFaculty) {
            ArticleFaculty articleFaculty = new ArticleFaculty();
            articleFaculty.setFaculty(this);
            articleFaculty.setArticle(article);

            articleFaculties.add(articleFaculty);
            article.getArticleFaculties().add(articleFaculty); // Maintain both sides
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Faculty faculty)) return false;
        return id != null && id.equals(faculty.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
