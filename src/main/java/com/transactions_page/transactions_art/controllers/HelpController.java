package com.transactions_page.transactions_art.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelpController {
    @GetMapping("/help")
    public String showHelpPage() {
        return "help";
    }
}