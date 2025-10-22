package com.sdgs.itb.entity.carousel;

import com.fasterxml.jackson.databind.JsonNode;
import com.sdgs.itb.common.converter.JsonNodeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "carousel", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carousel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carousel_id_gen")
    @SequenceGenerator(name = "carousel_id_gen", sequenceName = "carousel_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String subtitle;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "source_url")
    private String sourceUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> cropData;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carousel_category_id")
    private CarouselCategory carouselCategory;

    @OneToMany(mappedBy = "carousel", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CarouselGoal> carouselGoals = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carousel carousel)) return false;
        return id != null && id.equals(carousel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
