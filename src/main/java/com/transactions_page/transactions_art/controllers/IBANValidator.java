package com.transactions_page.transactions_art.controllers;

import java.math.BigInteger;

public class IBANValidator {
    
    public static String calculateCheckDigits(String iban) {
        // Step 1: Move first four characters to end
        String movedIban = iban.substring(4) + iban.substring(0, 4);
        
        // Step 2: Replace letters with corresponding numbers
        StringBuilder ibanDigits = new StringBuilder();
        for (char ch : movedIban.toCharArray()) {
            if (Character.isDigit(ch)) {
                ibanDigits.append(ch);
            } else {
                ibanDigits.append(Character.getNumericValue(ch) + 9);
            }
        }
        
        // Step 3: Convert IBAN to numerical string
        BigInteger ibanNumerical = new BigInteger(ibanDigits.toString());
        
        // Step 4: Calculate remainder when divided by 97
        BigInteger remainder = ibanNumerical.mod(BigInteger.valueOf(97));
        
        // Step 5: Subtract remainder from 98
        int checkDigits = 98 - remainder.intValue();
        
        return String.format("%02d", checkDigits); // Ensure result is a two-digit string with leading zero if necessary
    }
}
