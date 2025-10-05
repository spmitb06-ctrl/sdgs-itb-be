package com.sdgs.itb.service.unit.specification;

import com.sdgs.itb.entity.unit.Unit;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UnitSpecification {

    public static Specification<Unit> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Unit> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Unit> hasTypes(List<Long> typeIds) {
        return (root, query, cb) -> {
            if (typeIds == null || typeIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("unitType").get("id").in(typeIds);
        };
    }
}
