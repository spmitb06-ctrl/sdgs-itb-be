package com.sdgs.itb.service.data.specification;

import com.sdgs.itb.entity.data.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class DataSpecification {

    public static Specification<Data> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Data> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Data> hasGoal(List<Long> goalIds) {
        return (root, query, cb) -> {
            if (goalIds == null || goalIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("dataGoals")
                    .join("goal")
                    .get("id")
                    .in(goalIds);
        };
    }

    public static Specification<Data> hasCategories(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("dataCategory").get("id").in(categoryIds);
        };
    }

    public static Specification<Data> hasUnit(List<Long> unitIds) {
        return (root, query, cb) -> {
            if (unitIds == null || unitIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("dataUnits")
                    .join("unit")
                    .get("id")
                    .in(unitIds);
        };
    }

    public static Specification<Data> hasYear(String year) {
        return (root, query, cb) -> {
            if (year == null || year.isEmpty()) {
                return cb.conjunction();
            }
            try {
                int yearInt = Integer.parseInt(year);
                return cb.equal(cb.function("YEAR", Integer.class, root.get("eventDate")), yearInt);
            } catch (NumberFormatException e) {
                return cb.conjunction();
            }
        };
    }

}
