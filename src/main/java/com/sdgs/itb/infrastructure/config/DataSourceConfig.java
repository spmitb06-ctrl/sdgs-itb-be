package com.sdgs.itb.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;

import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Value("${POSTGRES_USER:}")
    private String username;

    @Value("${POSTGRES_PASSWORD:}")
    private String password;

    @Bean
    public DataSource dataSource() throws Exception {
        if (StringUtils.hasText(databaseUrl)) {
            // Convert Railway DATABASE_URL to JDBC URL
            URI dbUri = new URI(databaseUrl);
            String user = username != null && !username.isEmpty() ? username : dbUri.getUserInfo().split(":")[0];
            String pass = password != null && !password.isEmpty() ? password : dbUri.getUserInfo().split(":")[1];
            String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();

            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(user)
                    .password(pass)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        } else {
            throw new IllegalStateException("DATABASE_URL environment variable is missing");
        }
    }
}

