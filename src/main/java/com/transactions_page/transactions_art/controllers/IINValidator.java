package com.transactions_page.transactions_art.controllers;

public class IINValidator {
    public String getCardBranch(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return "Visa";
        } else if (cardNumber.substring(0, 2).matches("5[1-5]")) {
            return "MasterCard";
        } else {
            return "Unknown";
        }
    }
}