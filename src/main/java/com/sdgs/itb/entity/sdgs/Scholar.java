package com.sdgs.itb.entity.sdgs;

import com.sdgs.itb.entity.news.Article;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "scholars", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scholar {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scholar_id_gen")
    @SequenceGenerator(name = "scholar_id_gen", sequenceName = "scholar_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

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

    // Relationships
    @OneToMany(mappedBy = "scholar", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GoalScholar> goalScholars = new HashSet<>();

    @OneToMany(mappedBy = "scholar", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scholar scholar)) return false;
        return id != null && id.equals(scholar.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
