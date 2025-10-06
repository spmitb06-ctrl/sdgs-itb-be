package com.sdgs.itb.entity.report;

import com.sdgs.itb.entity.report.ReportCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "reports", schema = "sdgs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_id_gen")
    @SequenceGenerator(name = "report_id_gen", sequenceName = "report_id_seq", schema = "sdgs", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_url")
    private String fileUrl;

    @Column
    private String year;

    @Column(name="image_url")
    private String imageUrl;

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
    @JoinColumn(name = "report_category_id")
    private ReportCategory reportCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report report)) return false;
        return id != null && id.equals(report.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
