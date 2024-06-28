package com.transactions_page.transactions_art.controllers;

import com.transactions_page.transactions_art.models.Account;
import com.transactions_page.transactions_art.models.Country;
import com.transactions_page.transactions_art.models.Bank;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private CountryService countryService;

    @Autowired
    private BankService bankService;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        List<Country> countries = countryService.findAll();
        model.addAttribute("countries", countries);
        List<Bank> banks = bankService.findAll();
        model.addAttribute("banks", banks);
        model.addAttribute("account", new Account());
        return "registration";
    }

    @PostMapping("/register")
    public String handleRegistration(@RequestParam("country") int countryId,
                                    @RequestParam("bank") int bankId,
                                    @RequestParam("phone") String phoneNumber, 
                                    @RequestParam("accDOB") String accDOBtest,
                                    @RequestParam("accDOB") @DateTimeFormat(pattern = "yyyy-MM-dd") Date accDOB, 
                                    @ModelAttribute Account account, Model model) 
    {
        System.out.println("accDOB: " + countryId + " " + bankId + " " + phoneNumber + " " + accDOBtest);

        Country country = countryService.findById(countryId);
        int countryPhoneCode = country.getCountryPhoneCode();
        account.setCountry(country);
    
        Bank bank = bankService.findById(bankId);
        account.setBank(bank);

        String fullPhoneNumber = "+" + countryPhoneCode + phoneNumber;
        account.setAccPhone(fullPhoneNumber);

        if (accountService.existsByAccEmailOrAccPhone(account.getAccEmail(), account.getAccPhone())) {
            model.addAttribute("error", "An account with the same email or phone number already exists");
            return "registration";
        }
        LocalDate localAccDOB = accDOB.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (!localAccDOB.isBefore(LocalDate.now().minusYears(18))) {
            model.addAttribute("error", "Registering client must be older than 18 y.o.");
            // Return to the registration page with error message
            List<Country> countries = countryService.findAll();
            model.addAttribute("countries", countries);
            List<Bank> banks = bankService.findAll();
            model.addAttribute("banks", banks);
            model.addAttribute("account", account);
            return "registration";
        }
        
        // Fetch the latest account ID
        String fetchLatestIdSql = "SELECT MAX(acc_id) FROM account";
        Integer latestAccountId = jdbcTemplate.queryForObject(fetchLatestIdSql, Integer.class);

        // Set the new account ID
        account.setAccId(latestAccountId != null ? latestAccountId + 1 : 1);

        account.setAccBalance(50.0);
        Date now = new Date();
        account.setAccCreationDate(now);
        account.setAccUpdatedDate(now);
        account.setStatus(Account.AccountStatus.active);

        // Generate account number using accDOB and acc_creation_date
        String accountNumber = ANValidator.generateAccountNumber(accDOB, now);
        long accountNumberLong = Long.parseLong(accountNumber);
        account.setAccNum(accountNumberLong);

        // Calculate IBAN check digits
        String ibanCheckDigits = IBANValidator.calculateCheckDigits(bank.getBankNum() + "00" + country.getCountryCode());

        // Construct IBAN
        String iban = country.getCountryCode() + ibanCheckDigits + bank.getBankNum() + accountNumber;

        account.setAccIban(iban);

        accountService.save(account);
        return "redirect:/";
    }
}