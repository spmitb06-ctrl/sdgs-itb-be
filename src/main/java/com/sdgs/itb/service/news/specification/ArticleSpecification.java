package com.sdgs.itb.service.news.specification;

import com.sdgs.itb.entity.news.Article;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ArticleSpecification {

    public static Specification<Article> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Article> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Article> hasGoal(List<Long> goalIds) {
        return (root, query, cb) -> {
            if (goalIds == null || goalIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("articleGoals")
                    .join("goal")
                    .get("id")
                    .in(goalIds);
        };
    }

    public static Specification<Article> hasCategories(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("articleCategory").get("id").in(categoryIds);
        };
    }

    public static Specification<Article> hasScholars(List<Long> scholarIds) {
        return (root, query, cb) -> {
            if (scholarIds == null || scholarIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("scholar").get("id").in(scholarIds);
        };
    }

    public static Specification<Article> hasFaculty(List<Long> facultyIds) {
        return (root, query, cb) -> {
            if (facultyIds == null || facultyIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("articleFaculties")
                    .join("faculty")
                    .get("id")
                    .in(facultyIds);
        };
    }

    public static Specification<Article> hasYear(String year) {
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
