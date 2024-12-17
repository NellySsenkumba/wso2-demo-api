package org.nelly.wso2demoapi.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Generator {
    public Long generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder("214"); // First two digits fixed as '11'
        long timestamp = System.currentTimeMillis();
        accountNumber.append(String.valueOf(timestamp).substring(0, 10)); // Add the first 10 digits of the timestamp
        Random random = new Random();
        while (accountNumber.length() < 14) {
            accountNumber.append(random.nextInt(10)); // Add random digits until the length is 20
        }
        return Long.parseLong(accountNumber.toString());
    }

    public String generateTransactionReference() {
        return "TXN-" + java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12).toUpperCase();
    }
}
