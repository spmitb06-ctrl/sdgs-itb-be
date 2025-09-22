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
                Role adminWebRole = new Role();
                adminWebRole.setName(RoleType.ADMIN_WEB);
                roleRepository.save(adminWebRole);

                Role adminFacultyRole = new Role();
                adminFacultyRole.setName(RoleType.ADMIN_FACULTY);
                roleRepository.save(adminFacultyRole);

            }
        };
    }
}
