package com.transactions_page.transactions_art.controllers;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.jdbc.core.JdbcTemplate;

import com.transactions_page.transactions_art.models.Account;
import com.transactions_page.transactions_art.models.Country;
import com.transactions_page.transactions_art.models.Notes;
import com.transactions_page.transactions_art.controllers.CountryService;
import com.transactions_page.transactions_art.controllers.NotesService;
import com.transactions_page.transactions_art.controllers.CardService;
import com.transactions_page.transactions_art.controllers.TransactionService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private NotesService notesService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        DatabaseInitializer databaseInitializer = new DatabaseInitializer();
        databaseInitializer.initializeDatabase();
    }

    @GetMapping("/")
    public String showLoginPage(Model model) {
        List<Country> countries = countryService.findAll();
        model.addAttribute("countries", countries);
        return "login";
    }

    @PostMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("phone-code") String countryCode, 
                              @RequestParam("phone") String phoneNumber, 
                              @RequestParam("password") String password, 
                              Model model, 
                              RedirectAttributes redirectAttributes, 
                              HttpSession session) 
    {
        String fullPhoneNumber = "+" + countryCode + phoneNumber;
        
        Account account = accountService.findByAccPhone(fullPhoneNumber);
        List<Country> countries = countryService.findAll();
        model.addAttribute("countries", countries);
        if (account != null && account.getAccPassword().equals(password)) {
            // Check if account is active
            if (account.getStatus() == Account.AccountStatus.inactive) {
                model.addAttribute("error", "Account is not active.");
                return "login";
            }
            session.setAttribute("loggedInAccountId", account.getAccId());
            redirectAttributes.addFlashAttribute("account", account);
            return "redirect:/account";
        } else {
            model.addAttribute("error", "Invalid phone or password");
            return "login";
        }
    }

    @GetMapping("/account")
    public String showAccountPage(Model model, HttpSession session) {
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        if (loggedInAccountId != null) {
            Optional<Account> accountOptional = accountService.findById(loggedInAccountId);
            if (accountOptional.isPresent()) {
                Double totalSent = transactionService.getTotalAmountSentByAccountId(loggedInAccountId);
                if (totalSent == null) {
                    totalSent = 0.0;
                }
                model.addAttribute("totalSent", totalSent);
                Account account = accountOptional.get();
                model.addAttribute("account", account);

                // Fetch note for the logged in account
                Optional<Notes> noteOptional = notesService.findByAccount(account);
                if (noteOptional.isPresent()) {
                    Notes note = noteOptional.get();
                    model.addAttribute("note", note);
                } else {
                    model.addAttribute("note", new Notes());
                }
                
            }
        }
        return "account_home";
    }

    @PostMapping("/updateNote")
    public String updateNote(@RequestParam("note") String description, HttpSession session) {
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        if (loggedInAccountId != null) {
            Optional<Account> accountOptional = accountService.findById(loggedInAccountId);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                Optional<Notes> noteOptional = notesService.findByAccount(account);
                Notes note;
                if (noteOptional.isPresent()) {
                    // If a note exists, update it
                    note = noteOptional.get();
                } else {
                    // If no note exists, create a new one
                    note = new Notes();
                    note.setAccount(account);
                }
                note.setDescription(description);
                notesService.save(note);
            }
        }
        return "redirect:/account";
    }
    
    @PostMapping("/deleteNote")
    @Transactional
    public String deleteNote(HttpSession session) {
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        if (loggedInAccountId != null) {
            Optional<Account> accountOptional = accountService.findById(loggedInAccountId);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                notesService.deleteByAccount(account);
            }
        }
        return "redirect:/account";
    }


    @PostMapping("/deleteAccount")
    public String deleteAccount(HttpSession session) {
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
    
        // Update all cards associated with the logged-in account to inactive
        String updateCardsQuery = "UPDATE card SET status = 'inactive' WHERE account_id = ?";
        jdbcTemplate.update(updateCardsQuery, loggedInAccountId);

        // Update the account to inactive
        String updateAccountQuery = "UPDATE account SET status = 'inactive' WHERE acc_id = ?";
        jdbcTemplate.update(updateAccountQuery, loggedInAccountId);

        session.invalidate();
        return "redirect:/";
    }
    
}