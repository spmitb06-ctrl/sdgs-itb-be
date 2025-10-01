package com.sdgs.itb.service.news.specification;

import com.sdgs.itb.entity.news.News;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class NewsSpecification {

    public static Specification<News> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<News> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<News> hasGoal(List<Long> goalIds) {
        return (root, query, cb) -> {
            if (goalIds == null || goalIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("newsGoals")
                    .join("goal")
                    .get("id")
                    .in(goalIds);
        };
    }

    public static Specification<News> hasCategories(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("newsCategory").get("id").in(categoryIds);
        };
    }

    public static Specification<News> hasScholars(List<Long> scholarIds) {
        return (root, query, cb) -> {
            if (scholarIds == null || scholarIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("scholar").get("id").in(scholarIds);
        };
    }

    public static Specification<News> hasUnit(List<Long> unitIds) {
        return (root, query, cb) -> {
            if (unitIds == null || unitIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("newsUnits")
                    .join("unit")
                    .get("id")
                    .in(unitIds);
        };
    }

    public static Specification<News> hasYear(String year) {
        return (root, query, cb) -> {
            if (year == null || year.isEmpty()) {
                return cb.conjunction();
            }
            // Convert year string to integer
            int yearInt = Integer.parseInt(year);
            return cb.equal(cb.function("YEAR", Integer.class, root.get("createdAt")), yearInt);
        };
    }
}
