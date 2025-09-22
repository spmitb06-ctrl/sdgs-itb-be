package com.sdgs.itb.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        // You can configure real SMTP if needed
        return new org.springframework.mail.javamail.JavaMailSenderImpl();
    }
}

