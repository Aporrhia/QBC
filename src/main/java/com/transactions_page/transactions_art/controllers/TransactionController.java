package com.transactions_page.transactions_art.controllers;

import org.apache.catalina.filters.HttpHeaderSecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.transactions_page.transactions_art.models.Account;
import com.transactions_page.transactions_art.models.Transaction;
import com.transactions_page.transactions_art.models.Card;

import jakarta.servlet.http.HttpSession;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private CardService cardService;

    @Autowired
    private HttpSession session;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/transactions")
    public String showTransactionPage(Model model, HttpSession session, 
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(required = false) String filter) {
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        if (loggedInAccountId != null) {

            // Sorting
            if (sort != null) {
                session.setAttribute("sort", sort);
            } else {
                sort = (String) session.getAttribute("sort");
                if (sort == null) {
                    sort = "desc"; // default sort order
                }
            }

            // Filtering
            if (filter != null) {
                session.setAttribute("filter", filter);
            } else {
                filter = (String) session.getAttribute("filter");
            }

            Pageable pageable = PageRequest.of(page - 1, 15, Sort.by(sort.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "transactionDate"));
            Page<Transaction> transactions;
            if (filter != null) {
                switch (filter) {
                    case "0-50":
                        transactions = transactionService.findBySenderAccountAccIdOrRecipientAccountAccIdAndAmountBetween(loggedInAccountId, 0.0, 50.0, pageable);
                        break;
                    case "50-250":
                        transactions = transactionService.findBySenderAccountAccIdOrRecipientAccountAccIdAndAmountBetween(loggedInAccountId, 50.0, 250.0, pageable);
                        break;
                    case "250-max":
                        transactions = transactionService.findBySenderAccountAccIdOrRecipientAccountAccIdAndAmountGreaterThan(loggedInAccountId, 250.0, pageable);
                        break;
                    default:
                        transactions = sort.equals("asc") ?
                            transactionService.findBySenderAccountAccIdOrRecipientAccountAccIdOrderByTransactionDateAsc(loggedInAccountId, loggedInAccountId, pageable) :
                            transactionService.findBySenderAccountAccIdOrRecipientAccountAccIdOrderByTransactionDateDesc(loggedInAccountId, loggedInAccountId, pageable);
                }
            } else {
                transactions = sort.equals("asc") ?
                    transactionService.findBySenderAccountAccIdOrRecipientAccountAccIdOrderByTransactionDateAsc(loggedInAccountId, loggedInAccountId, pageable) :
                    transactionService.findBySenderAccountAccIdOrRecipientAccountAccIdOrderByTransactionDateDesc(loggedInAccountId, loggedInAccountId, pageable);
            }

            model.addAttribute("transactions", transactions.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", transactions.getTotalPages());
            
            // Calculate pagination numbers without duplicates
            int totalPages = transactions.getTotalPages();
            int currentPage = page;
            int startPage = Math.max(1, currentPage - 2);
            int endPage = Math.min(startPage + 4, totalPages);
            int offset = Math.max(0, 4 - (endPage - startPage));
            startPage = Math.max(1, startPage - offset);
            endPage = Math.min(totalPages, endPage + offset);
            List<Integer> paginationNumbers = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
            // Remove the first and last page from paginationNumbers
            if (paginationNumbers.size() > 2) {
                paginationNumbers = paginationNumbers.subList(1, paginationNumbers.size() - 1);
            }
            
            model.addAttribute("paginationNumbers", paginationNumbers);
        }
        return "transaction";
    }
    

    
    @GetMapping("/transactions/details")
    public String showTransactionDetails(@RequestParam("id") Integer id, Model model) {
        Optional<Transaction> transactionOptional = transactionService.findById(id);
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            model.addAttribute("transaction", transaction);
            return "transaction_details";
        } else {
            return "redirect:/transactions";
        }
    }

    @GetMapping("/transactions/form")
    public String showNewTransactionForm(Model model, HttpSession session) {
        model.addAttribute("transaction", new Transaction());

        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        if (loggedInAccountId != null) {
            List<Card> cards = cardService.findByAccountAccId(loggedInAccountId.intValue());
            List<Card> activeCards = cards.stream()
                                          .filter(card -> card.getStatus() == Card.CardStatus.active)
                                          .collect(Collectors.toList());
            model.addAttribute("cards", activeCards);
        }

        return "tr_operation";
    }

    @PostMapping("/transactions/send")
    public String sendTransaction(@ModelAttribute("transaction") Transaction transaction, RedirectAttributes redirectAttributes, HttpSession session, Model model) {
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        Optional<Account> senderAccountOptional = accountService.findById(loggedInAccountId);
        Account senderAccount = senderAccountOptional.get();
        // Fetch recipient's account details from the form
        Account recipientAccount = accountService.getAccountByAccFnameAndAccLnameAndAccIban(
            transaction.getRecipientAccount().getAccFname(),
            transaction.getRecipientAccount().getAccLname(),
            transaction.getRecipientAccount().getAccIban()
        );
        // Check if recipient's account details exist
        if (recipientAccount == null || recipientAccount.getStatus() == Account.AccountStatus.inactive) {
            System.out.println("Recipient's status" + recipientAccount.getStatus());
            model.addAttribute("error", "Recipient's account details do not exist. Please check the details and try again.");
            return "tr_operation";
        }


        double senderNewBalance = senderAccount.getAccBalance() - transaction.getAmount();

        // Check if sender has enough balance
        if (senderNewBalance < 0) {
            model.addAttribute("error", "Insufficient balance. Please check the amount and try again.");
            return "tr_operation";
        }
        senderAccount.setBalance(senderNewBalance);
    
        // Add the transaction amount to the recipient's account balance
        double recipientNewBalance = recipientAccount.getAccBalance() + transaction.getAmount();
        recipientAccount.setBalance(recipientNewBalance);
    
        // Save the updated accounts to the database
        accountService.save(senderAccount);
        accountService.save(recipientAccount);


        // Fetch the sender's card from the database
        Card senderCard = cardService.findByNumber(transaction.getSenderCard().getNumber());
        if (senderCard == null || senderCard.getStatus() == Card.CardStatus.inactive) {
            System.out.println("Card's status" + senderCard.getStatus());
            model.addAttribute("error", "Sender's card details do not exist. Please check the details and try again.");
            return "tr_operation";
        }

        // Fetch the recipient's card from the database
        Card recipientCard = cardService.findByNumber(transaction.getRecipientCard().getNumber());
        if (recipientCard == null || recipientCard.getStatus() == Card.CardStatus.inactive){
            model.addAttribute("error", "Recipient's card details do not exist. Please check the details and try again.");
            return "tr_operation";
        }

        // Check if sender's card number is the same as recipient's card number
        if (senderCard.getNumber().equals(recipientCard.getNumber())) {
            model.addAttribute("error", "You cannot send money to yourself. Please check the recipient's card details and try again.");
            return "tr_operation";
        }

        // Fetch the latest transaction ID
        String fetchLatestIdSql = "SELECT MAX(tr_id) FROM transaction";
        Integer latestTransactionId = jdbcTemplate.queryForObject(fetchLatestIdSql, Integer.class);

        // Set the new transaction ID
        transaction.setId(latestTransactionId != null ? latestTransactionId + 1 : 1);

        // Set the sender and recipient accounts and cards to the transaction
        transaction.setSenderAccount(senderAccount);
        transaction.setRecipientAccount(recipientAccount);
        transaction.setSenderCard(senderCard);
        transaction.setRecipientCard(recipientCard);
        transaction.setTransactionDate(new Date());
        transaction.setStatus(Transaction.TransactionStatus.Completed);

        // Save the transaction to the database
        Transaction savedTransaction = transactionService.save(transaction);

    
        // Check if the transaction was saved successfully
        if (savedTransaction != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Transaction successful!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Transaction failed. Please check the details and try again.");
        }
    
        // Redirect the user to the transactions page
        return "redirect:/transactions";
    }


    @PostMapping("/transactions/pdf")
    public String generatePdfAndRedirect(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        Optional<Account> senderAccountOptional = accountService.findById(loggedInAccountId);
        Optional<Transaction> transactionOptional = transactionService.findById(id);
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();

            // Generate PDF content
            PDFGenerator pdfGenerator = new PDFGenerator();
            byte[] pdfData = pdfGenerator.generatePdf(transaction, senderAccountOptional);

            // Store the PDF data in a flash attribute
            redirectAttributes.addFlashAttribute("pdfData", pdfData);


            // Redirect to the download page
            return "redirect:/transactions";
        } else {
            // Redirect to an error page if the transaction is not found
            return "redirect:/error";
        }
    }

    @GetMapping("/transactions/downloadPdf")
    public ResponseEntity<byte[]> downloadPdf(@ModelAttribute("pdfData") byte[] pdfData) {
        Integer loggedInAccountId = (Integer) session.getAttribute("loggedInAccountId");
        Integer id = (Integer) session.getAttribute("id"); // get the id from the session

        // Retrieve the Account object for the logged in account
        Optional<Account> loggedInAccountOptional = accountService.findById(loggedInAccountId);
        if (!loggedInAccountOptional.isPresent()) {
            // Handle the case where the account is not found
        }
        Account loggedInAccount = loggedInAccountOptional.get();

        // Get the accDOB from the Account object
        Date accDOB = loggedInAccount.getAccDOB();

        // Set up HTTP headers for the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Get the current date and format it
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = sdf.format(new Date());

        Date now = new Date();
        String accountNumber = Long.toString(loggedInAccount.getAccNum());
        String filename = currentDate + "_" + accountNumber + ".pdf";

        headers.setContentDispositionFormData("filename", filename);

        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
    }
}
