package com.transactions_page.transactions_art.controllers;

import org.springframework.data.jpa.repository.JpaRepository;
import com.transactions_page.transactions_art.models.Bank;

public interface BankService extends JpaRepository<Bank, Integer> {
    Bank findById(int bankId);
}