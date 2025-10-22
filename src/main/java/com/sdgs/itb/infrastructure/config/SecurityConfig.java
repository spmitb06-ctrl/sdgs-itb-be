package com.sdgs.itb.infrastructure.config;

import com.sdgs.itb.entity.user.RoleType;
import com.sdgs.itb.infrastructure.auth.filters.TokenBlacklist;
import com.sdgs.itb.service.auth.GetUserAuthDetailsService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Log
public class SecurityConfig {

    private final GetUserAuthDetailsService getUserAuthDetailsService;
    private final JwtConfigProperties jwtConfigProperties;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklist tokenBlacklistFilter;

    public SecurityConfig(
            GetUserAuthDetailsService getUserAuthDetailsService,
            JwtConfigProperties jwtConfigProperties,
            PasswordEncoder passwordEncoder,
            TokenBlacklist tokenBlacklistFilter) {
        this.getUserAuthDetailsService = getUserAuthDetailsService;
        this.jwtConfigProperties = jwtConfigProperties;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistFilter = tokenBlacklistFilter;
    }

    @Bean
    public AuthenticationManager authManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(getUserAuthDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(new CorsConfigurationSourceImpl()))
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers("/error/**").permitAll()
//                        .requestMatchers("/api/v1/user/register/**").permitAll()
//                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/admins").permitAll()
                        .requestMatchers("/api/v1/admins/**").permitAll()
                        .requestMatchers("/api/v1/roles").permitAll()
                        .requestMatchers("/api/v1/news").permitAll()
                        .requestMatchers("/api/v1/news/**").permitAll()
                        .requestMatchers("/api/v1/news-categories").permitAll()
                        .requestMatchers("/api/v1/news-categories/**").permitAll()
                        .requestMatchers("/api/v1/policy-categories").permitAll()
                        .requestMatchers("/api/v1/policy-categories/**").permitAll()
                        .requestMatchers("/api/v1/report-categories").permitAll()
                        .requestMatchers("/api/v1/report-categories/**").permitAll()
                        .requestMatchers("/api/v1/carousel-categories").permitAll()
                        .requestMatchers("/api/v1/carousel-categories/**").permitAll()
                        .requestMatchers("/api/v1/units/**").permitAll()
                        .requestMatchers("/api/v1/unit-type").permitAll()
                        .requestMatchers("/api/v1/unit-type/**").permitAll()
                        .requestMatchers("/api/v1/goals/**").permitAll()
                        .requestMatchers("/api/v1/scholars").permitAll()
                        .requestMatchers("/api/v1/scholars/**").permitAll()
                        .requestMatchers("/api/v1/policies").permitAll()
                        .requestMatchers("/api/v1/policies/**").permitAll()
                        .requestMatchers("/api/v1/reports").permitAll()
                        .requestMatchers("/api/v1/reports/**").permitAll()
                        .requestMatchers("/api/v1/carousel").permitAll()
                        .requestMatchers("/api/v1/carousel/**").permitAll()
                        .requestMatchers("/api/v1/goal-scholars").permitAll()
                        .requestMatchers("/api/v1/goal-scholars/**").permitAll()
                        .requestMatchers("/api/v1/files").permitAll()
                        .requestMatchers("/api/v1/files/**").permitAll()
                        .requestMatchers("/api/v1/typesense/**").permitAll()
                        .requestMatchers("/api/v1/community-service/**").permitAll()
                        .requestMatchers("/api/v1/data").permitAll()
                        .requestMatchers("/api/v1/data/**").permitAll()
                        .requestMatchers("/api/v1/data/categories").permitAll()
                        .requestMatchers("/api/v1/data/categories/**").permitAll()

                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            jwt.decoder(jwtDecoder());
                            jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()); // ✅ FIXED
                        })
                        .bearerTokenResolver(request -> {
                            // ✅ Get token from cookie first
                            if (request.getCookies() != null) {
                                for (Cookie cookie : request.getCookies()) {
                                    if ("SID".equals(cookie.getName())) {
                                        return cookie.getValue();
                                    }
                                }
                            }
                            // ✅ Fallback to Authorization header
                            String header = request.getHeader("Authorization");
                            return header != null ? header.replace("Bearer ", "") : null;
                        })
                )
                .addFilterAfter(tokenBlacklistFilter, BearerTokenAuthenticationFilter.class)
                .userDetailsService(getUserAuthDetailsService)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(jwtConfigProperties.getSecret().getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey key = new SecretKeySpec(jwtConfigProperties.getSecret().getBytes(), "HmacSHA256");
        JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<>(key);
        return new NimbusJwtEncoder(immutableSecret);
    }

    // ✅ Custom converter to map "roles" claim to authorities
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object rolesClaim = jwt.getClaim("roles");
            if (rolesClaim == null) return List.of();

            String roles = rolesClaim.toString(); // e.g. "STUDENT" or "ADMIN,SUPERVISOR"
            log.info("Extracted roles from JWT: " + roles);

            return Arrays.stream(roles.split(","))
                    .map(String::trim)
                    .map(SimpleGrantedAuthority::new) // ✅ matches hasAuthority()
                    .collect(Collectors.toList());
        });
        return converter;
    }
}
