package com.transactions_page.transactions_art.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;


@Controller
public class GeneralController {
    @GetMapping("/general")
    public String showGeneralPage(Model model) {
        return "general";
    }
}