package com.transactions_page.transactions_art.controllers;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transactions_page.transactions_art.controllers.AccountService;
import com.transactions_page.transactions_art.models.Account;

public interface AccountService extends JpaRepository<Account, Integer> {
    Account findByAccPhone(String accPhone);
    Account findById(int accId);
    Account findByAccIban(String accIban);
    Account getAccountByAccFnameAndAccLnameAndAccIban(String accFname, String accLname, String accIban);
    Account getAccountByAccFnameAndAccLname(String accFname, String accLname);
    Account getAccountByAccIban(String accIban);
    boolean existsByAccEmailOrAccPhone(String accEmail, String accPhone);
}