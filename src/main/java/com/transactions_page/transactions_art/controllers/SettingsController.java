package com.transactions_page.transactions_art.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.transactions_page.transactions_art.models.Account;

import jakarta.servlet.http.HttpSession;

@Controller
public class SettingsController {
    
    @Autowired
    private AccountService accountService;

    @GetMapping("/settings")
    public String showAccountPage(Model model, HttpSession session) {
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        if (loggedInAccountId != null) {
            Optional<Account> accountOptional = accountService.findById(loggedInAccountId);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                model.addAttribute("account", account);
            }
        }
        return "settings";
    }

}
