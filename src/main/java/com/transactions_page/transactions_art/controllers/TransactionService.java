package com.transactions_page.transactions_art.controllers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import com.transactions_page.transactions_art.controllers.AccountService;
import com.transactions_page.transactions_art.models.Account;
import com.transactions_page.transactions_art.models.Transaction;

public interface TransactionService extends JpaRepository<Transaction, Integer> {
    List<Transaction> findBySenderAccountAccIdOrRecipientAccountAccId(int senderAccId, int recipientAccId);
    Page<Transaction> findBySenderAccountAccIdOrRecipientAccountAccId(Integer senderAccId, Integer recipientAccId, Pageable pageable);
    Transaction findById(int id);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.senderAccount.accId = :accountId")
    public abstract Double getTotalAmountSentByAccountId(@Param("accountId") Integer accountId);

    Page<Transaction> findBySenderAccountAccIdOrRecipientAccountAccIdOrderByTransactionDateAsc(Integer senderAccId, Integer recipientAccId, Pageable pageable);
    Page<Transaction> findBySenderAccountAccIdOrRecipientAccountAccIdOrderByTransactionDateDesc(Integer senderAccId, Integer recipientAccId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.senderAccount.accId = :accId OR t.recipientAccount.accId = :accId) AND t.amount BETWEEN :minAmount AND :maxAmount")
    Page<Transaction> findBySenderAccountAccIdOrRecipientAccountAccIdAndAmountBetween(@Param("accId") Integer accId, @Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE (t.senderAccount.accId = :accId OR t.recipientAccount.accId = :accId) AND t.amount > :minAmount")
    Page<Transaction> findBySenderAccountAccIdOrRecipientAccountAccIdAndAmountGreaterThan(@Param("accId") Integer accId, @Param("minAmount") Double minAmount, Pageable pageable);
}