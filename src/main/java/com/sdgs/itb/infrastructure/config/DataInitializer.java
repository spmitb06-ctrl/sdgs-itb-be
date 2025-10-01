package com.sdgs.itb.infrastructure.config;

import com.sdgs.itb.entity.user.Role;
import com.sdgs.itb.entity.user.RoleType;
import com.sdgs.itb.infrastructure.user.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role adminDevRole = new Role();
                adminDevRole.setName(RoleType.ADMIN_DEV);
                roleRepository.save(adminDevRole);

                Role adminWCURole = new Role();
                adminWCURole.setName(RoleType.ADMIN_WCU);
                roleRepository.save(adminWCURole);

                Role adminWebRole = new Role();
                adminWebRole.setName(RoleType.ADMIN_WEB);
                roleRepository.save(adminWebRole);

                Role adminUnitRole = new Role();
                adminUnitRole.setName(RoleType.ADMIN_UNIT);
                roleRepository.save(adminUnitRole);

                Role adminInternalRole = new Role();
                adminInternalRole.setName(RoleType.ADMIN_INTERNAL);
                roleRepository.save(adminInternalRole);
            }
        };
    }
}
