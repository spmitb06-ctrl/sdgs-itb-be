package com.sdgs.itb.service.policy;

import com.sdgs.itb.infrastructure.policy.dto.PolicyDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PolicyService {
    PolicyDTO createPolicy(PolicyDTO dto);
    PolicyDTO updatePolicy(Long id, PolicyDTO dto);
    void deletePolicy(Long id);
    PolicyDTO getPolicy(Long id);
    Page<PolicyDTO> getPolicies(
            String title,
            String year,
            List<Long> categoryIds,
            String sortBy,
            String sortDir,
            int page,
            int size
    );
}
