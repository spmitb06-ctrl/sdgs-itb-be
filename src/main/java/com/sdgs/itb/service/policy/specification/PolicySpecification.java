package com.sdgs.itb.service.policy.specification;

import com.sdgs.itb.entity.policy.Policy;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class PolicySpecification {

    public static Specification<Policy> notDeleted() {
        return (root, query, cb) ->
                cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Policy> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Policy> hasYear(String year) {
        return (root, query, cb) ->
                year == null ? null : cb.like(root.get("year"), "%" + year + "%");
    }

    public static Specification<Policy> hasGoal(List<Long> goalIds) {
        return (root, query, cb) -> {
            if (goalIds == null || goalIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("policyGoals")
                    .join("goal")
                    .get("id")
                    .in(goalIds);
        };
    }

    public static Specification<Policy> hasCategories(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("policyCategory").get("id").in(categoryIds);
        };
    }

}
