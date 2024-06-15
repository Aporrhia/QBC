package com.transactions_page.transactions_art.controllers;

import com.transactions_page.transactions_art.models.Account;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ANValidator {

    public static String generateAccountNumber(Date accDOB, Date accCreationDate) {
        // Format dates appropriately for concatenation
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dobString = dateFormat.format(accDOB);
        String creationDateString = dateFormat.format(accCreationDate);

        // Concatenate relevant portions of dates
        String concatenatedDates = dobString.substring(0, 4) + creationDateString.substring(4);

        // Generate random digits for the remaining portion of the account number
        StringBuilder accountNumber = new StringBuilder(concatenatedDates);
        Random random = new Random();
        while (accountNumber.length() < 12) { // Adjust the length as needed
            int digit = random.nextInt(10); // Generate random digit from 0 to 9
            accountNumber.append(digit);
        }

        return accountNumber.toString();
    }
}
