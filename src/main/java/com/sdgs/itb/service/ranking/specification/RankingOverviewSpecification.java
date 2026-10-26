package com.sdgs.itb.service.ranking.specification;

import com.sdgs.itb.entity.ranking.RankingOverview;
import org.springframework.data.jpa.domain.Specification;

public class RankingOverviewSpecification {

    public static Specification<RankingOverview> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<RankingOverview> hasSystemId(Long systemId) {
        return (root, query, cb) -> {
            if (systemId == null) return cb.conjunction();
            return cb.equal(root.get("rankingSystem").get("id"), systemId);
        };
    }

    public static Specification<RankingOverview> hasGoalId(Long goalId) {
        return (root, query, cb) -> {
            if (goalId == null) return cb.conjunction();
            return cb.equal(root.get("goal").get("id"), goalId);
        };
    }

    public static Specification<RankingOverview> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction();
            return cb.equal(root.get("year"), year);
        };
    }

    public static Specification<RankingOverview> isPublishedOnly(Boolean publishedOnly) {
        return (root, query, cb) -> {
            if (Boolean.TRUE.equals(publishedOnly)) {
                return cb.isTrue(root.get("isPublished"));
            }
            return cb.conjunction();
        };
    }
}