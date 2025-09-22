package com.sdgs.itb.entity.news;

import com.sdgs.itb.entity.faculty.Faculty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "article_faculties", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleFaculty {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_faculty_id_gen")
    @SequenceGenerator(name = "article_faculty_id_gen", sequenceName = "article_faculty_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

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
    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleFaculty that)) return false;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return article != null && faculty != null &&
                Objects.equals(article.getId(), that.article.getId()) &&
                Objects.equals(faculty.getId(), that.faculty.getId());
    }

    @Override
    public int hashCode() {
        return id != null
                ? id.hashCode()
                : Objects.hash(
                article != null ? article.getId() : 0,
                faculty != null ? faculty.getId() : 0
        );
    }

}


