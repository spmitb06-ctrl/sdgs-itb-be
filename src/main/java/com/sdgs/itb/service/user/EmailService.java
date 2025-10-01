package com.sdgs.itb.service.user;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendHtml(String to, String subject, String htmlContent) throws MessagingException;
    String loadEmailTemplate(String path, String link);
    void sendVerificationEmail(String to, String link) throws MessagingException;
    void sendPasswordChangeEmail(String to, String link) throws MessagingException;
    void sendPasswordResetEmail(String to, String link) throws MessagingException;
}
