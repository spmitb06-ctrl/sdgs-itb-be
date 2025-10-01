package com.sdgs.itb.entity.news;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "news_images", schema = "sdgs")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_image_id_gen")
    @SequenceGenerator(name = "news_image_id_gen", sequenceName = "news_image_id_seq", schema = "sdgs", allocationSize = 1)
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
    @JoinColumn(name = "news_id")
    private News news;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsImage that)) return false;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return news != null && Objects.equals(news.getId(), that.news.getId());
    }

    @Override
    public int hashCode() {
        return id != null
                ? id.hashCode()
                : Objects.hash(
                news != null ? news.getId() : 0
        );
    }
}

