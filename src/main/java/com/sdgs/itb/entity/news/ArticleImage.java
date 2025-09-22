package com.sdgs.itb.entity.news;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "article_images", schema = "sdgs")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_image_id_gen")
    @SequenceGenerator(name = "article_image_id_gen", sequenceName = "article_image_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    // Cloudinary (or other storage) URL
    private String imageUrl;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleImage that)) return false;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return article != null && Objects.equals(article.getId(), that.article.getId());
    }

    @Override
    public int hashCode() {
        return id != null
                ? id.hashCode()
                : Objects.hash(
                article != null ? article.getId() : 0
        );
    }
}

