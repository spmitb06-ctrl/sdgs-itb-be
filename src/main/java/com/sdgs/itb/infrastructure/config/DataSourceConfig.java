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

    @Value("${DATABASE_URL:}") // Heroku-style
    private String databaseUrl;

    @Value("${RAILWAY_DATABASE_URL:}") // Railway-style full URL (if exists)
    private String railwayDatabaseUrl;

    @Value("${PGHOST:}")
    private String host;

    @Value("${PGPORT:5432}")
    private String port;

    @Value("${PGDATABASE:}")
    private String database;

    @Value("${PGUSER:}")
    private String username;

    @Value("${PGPASSWORD:}")
    private String password;

    @Bean
    public DataSource dataSource() throws Exception {
        String jdbcUrl;
        String user;
        String pass;

        if (StringUtils.hasText(databaseUrl) || StringUtils.hasText(railwayDatabaseUrl)) {
            // Use DATABASE_URL or RAILWAY_DATABASE_URL if available
            String url = StringUtils.hasText(databaseUrl) ? databaseUrl : railwayDatabaseUrl;
            URI dbUri = new URI(url);
            user = username != null && !username.isEmpty() ? username : dbUri.getUserInfo().split(":")[0];
            pass = password != null && !password.isEmpty() ? password : dbUri.getUserInfo().split(":")[1];
            jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();
        } else if (StringUtils.hasText(host) && StringUtils.hasText(database)) {
            // Build JDBC URL from individual Railway env vars
            jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            user = username;
            pass = password;
        } else {
            throw new IllegalStateException("No database environment variables are set!");
        }

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(user)
                .password(pass)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
