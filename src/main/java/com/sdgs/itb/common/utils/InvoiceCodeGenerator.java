package com.sdgs.itb.common.utils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InvoiceCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateInvoiceCode(Long userId, Long clientId, LocalDate orderDate) {
        String formattedDate = orderDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomString = generateRandomAlphanumeric();
        return String.format("INV-%d-%d-%s-%s", userId, clientId, formattedDate, randomString);
    }

    private static String generateRandomAlphanumeric() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}


