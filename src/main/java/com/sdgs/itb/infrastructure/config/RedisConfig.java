package com.sdgs.itb.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.URI;
import java.time.Duration;

@Configuration
public class RedisConfig {

    // Mendukung REDIS_URL dari Railway
    @Value("${REDIS_URL:}")
    private String redisUrl;

    // Mendukung REDISHOST (sesuai screenshot Railway) dan REDIS_HOST
    @Value("${REDISHOST:${REDIS_HOST:localhost}}")
    private String redisHost;

    // Mendukung REDISPORT (sesuai screenshot Railway) dan REDIS_PORT
    @Value("${REDISPORT:${REDIS_PORT:6379}}")
    private int redisPort;

    // Mendukung REDISPASSWORD (sesuai screenshot Railway) dan REDIS_PASSWORD
    @Value("${REDISPASSWORD:${REDIS_PASSWORD:}}")
    private String redisPassword;

    @Value("${REDIS_DB:0}")
    private int redisDb;

    @Value("${REDIS_TIMEOUT:5000}")
    private int redisTimeout;

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        // 1. Cek apakah REDIS_URL tersedia dan valid
        if (redisUrl != null && !redisUrl.isBlank()) {
            try {
                URI uri = URI.create(redisUrl);
                configuration.setHostName(uri.getHost());
                configuration.setPort(uri.getPort() > 0 ? uri.getPort() : 6379);

                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":", 2);
                    String password = userInfo.length > 1 ? userInfo[1] : userInfo[0];
                    if (!password.isBlank()) {
                        configuration.setPassword(RedisPassword.of(password));
                    }
                }

                if (uri.getPath() != null && uri.getPath().length() > 1) {
                    try {
                        int db = Integer.parseInt(uri.getPath().substring(1));
                        configuration.setDatabase(db);
                    } catch (NumberFormatException ignored) {
                        configuration.setDatabase(redisDb);
                    }
                }
            } catch (Exception e) {
                applyHostAndPort(configuration);
            }
        } else {
            // 2. Gunakan REDISHOST, REDISPORT, REDISPASSWORD langsung dari Railway
            applyHostAndPort(configuration);
        }

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(redisTimeout))
                .build();

        return new LettuceConnectionFactory(configuration, clientConfig);
    }

    private void applyHostAndPort(RedisStandaloneConfiguration configuration) {
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);
        if (redisPassword != null && !redisPassword.isBlank()) {
            configuration.setPassword(RedisPassword.of(redisPassword));
        }
        configuration.setDatabase(redisDb);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}