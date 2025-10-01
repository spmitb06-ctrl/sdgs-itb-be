package com.sdgs.itb.service.report.specification;

import com.sdgs.itb.entity.report.Report;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ReportSpecification {

    public static Specification<Report> notDeleted() {
        return (root, query, cb) ->
                cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Report> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Report> hasYear(String year) {
        return (root, query, cb) ->
                year == null ? null : cb.like(root.get("year"), "%" + year + "%");
    }

    public static Specification<Report> hasCategories(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("reportCategory").get("id").in(categoryIds);
        };
    }

}
