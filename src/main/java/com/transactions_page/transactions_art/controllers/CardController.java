package com.transactions_page.transactions_art.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.transactions_page.transactions_art.controllers.CardService;
import com.transactions_page.transactions_art.controllers.AccountService;
import org.springframework.jdbc.core.JdbcTemplate;

import com.transactions_page.transactions_art.models.Account;
import com.transactions_page.transactions_art.models.Card;
import com.transactions_page.transactions_art.models.Card.CardType;
import com.transactions_page.transactions_art.models.Card.CardBranch;
import com.transactions_page.transactions_art.models.Card.CardStatus;

import jakarta.servlet.http.HttpSession;

@Controller
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private HttpSession session;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/cards")
    public String showTransactionPage(Model model, HttpSession session) {
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        if (loggedInAccountId != null) {
            List<Card> cards = cardService.findByAccountAccIdAndStatus(loggedInAccountId.intValue(), CardStatus.active);
            model.addAttribute("cards", cards);
        }
        return "card";
    }

    @PostMapping("/deleteCard")
    public String deleteCard(@RequestParam("id") int id) {
        String sql = "UPDATE card SET status = 'inactive' WHERE card_id = ?";
        jdbcTemplate.update(sql, id);
        return "redirect:/cards";
    }

    @PostMapping("/createCard")
    public String createCard(@RequestParam("type") String type,
                            @RequestParam("number") String number,
                            @RequestParam("expirationMonth") int expirationMonth,
                            @RequestParam("expirationYear") int expirationYear,
                            Principal principal,
                            Model model) {
        // Get the currently logged in account
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        if (loggedInAccountId == null) {
            model.addAttribute("error", "No account is currently logged in.");
            return "card";
        }
        Optional<Account> accountOptional = accountService.findById(loggedInAccountId);

        // Validate the card number
        IINValidator iinValidator = new IINValidator();
        String cardBranch = iinValidator.getCardBranch(number);
        if (!cardBranch.equals("Visa") && !cardBranch.equals("MasterCard")) {
            model.addAttribute("error", "Invalid card number. Please enter a Visa or MasterCard number.");
            return "card";
        }

        // Create a LocalDate from the selected month and year
        LocalDate expirationDate = LocalDate.of(expirationYear, expirationMonth, 1);

        // Check if the card's expiration date is less than a month from now
        if (expirationDate.isBefore(LocalDate.now().plusMonths(1))) {
            model.addAttribute("error", "Invalid expiration date. The expiration date must be at least a month from now.");
            return "card";
        }

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            // Fetch the latest card ID
            String fetchLatestIdSql = "SELECT MAX(card_id) FROM card";
            Integer latestCardId = jdbcTemplate.queryForObject(fetchLatestIdSql, Integer.class);

            // Create a new card
            Card card = new Card();
            card.setId(latestCardId != null ? latestCardId + 1 : 1);
            card.setType(CardType.valueOf(type));
            card.setBranch(CardBranch.valueOf(cardBranch));
            card.setNumber(number);
            card.setExpirationDate(Date.valueOf(expirationDate)); 
            card.setAccount(account); 
            card.setStatus(Card.CardStatus.active);

            // Save the new card
            cardService.save(card);
        }

        return "redirect:/cards";
    }
}