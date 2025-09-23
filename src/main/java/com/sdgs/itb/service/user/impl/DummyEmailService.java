package com.sdgs.itb.service.user.impl;

import com.sdgs.itb.service.user.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("docker") // Only active in Docker profile
public class DummyEmailService implements EmailService {

    @Override
    public void sendHtml(String to, String subject, String htmlContent) throws MessagingException {
        System.out.println("[DummyEmailService] sendHtml called for: " + to);
    }

    @Override
    public void sendVerificationEmail(String to, String verificationLink) throws MessagingException {
        System.out.println("[DummyEmailService] sendVerificationEmail called for: " + to);
    }

    @Override
    public void sendInvoiceEmail(String to, String subject, String body, byte[] pdfAttachment, String filename) throws MessagingException {
        System.out.println("[DummyEmailService] sendInvoiceEmail called for: " + to);
    }
}
