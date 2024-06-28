package com.transactions_page.transactions_art.controllers;

import java.math.BigInteger;

public class IBANValidator {
    
    public static String calculateCheckDigits(String iban) {
        // Move first four characters to end
        String movedIban = iban.substring(4) + iban.substring(0, 4);
        
        // Replace letters with corresponding numbers
        StringBuilder ibanDigits = new StringBuilder();
        for (char ch : movedIban.toCharArray()) {
            if (Character.isDigit(ch)) {
                ibanDigits.append(ch);
            } else {
                ibanDigits.append(Character.getNumericValue(ch) + 9);
            }
        }
        
        // Convert IBAN to numerical string
        BigInteger ibanNumerical = new BigInteger(ibanDigits.toString());
        
        // Calculate remainder when divided by 97
        BigInteger remainder = ibanNumerical.mod(BigInteger.valueOf(97));
        
        // Subtract remainder from 98
        int checkDigits = 98 - remainder.intValue();
        
        return String.format("%02d", checkDigits); // Result is a two-digit string with leading zero if necessary
    }
}