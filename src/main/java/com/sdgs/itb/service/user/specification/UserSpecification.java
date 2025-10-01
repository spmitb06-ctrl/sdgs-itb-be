package com.sdgs.itb.service.user.specification;

import com.sdgs.itb.entity.user.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UserSpecification {

    public static Specification<User> notDeleted() {
        return (root, query, cb) ->
                cb.isNull(root.get("deletedAt"));
    }

    public static Specification<User> hasRoles(List<Long> roleIds) {
        return (root, query, cb) -> {
            if (roleIds == null || roleIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("userRoles")
                    .join("role")
                    .get("id")
                    .in(roleIds);
        };
    }

    public static Specification<User> hasUnits(List<Long> unitIds) {
        return (root, query, cb) -> {
            if (unitIds == null || unitIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.join("unit").get("id").in(unitIds);
        };
    }

}
