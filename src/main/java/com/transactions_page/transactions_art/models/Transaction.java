package com.transactions_page.transactions_art.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "Transaction")
public class Transaction {

    @Id
    @Column(name = "tr_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_acc_id")
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(name = "recipient_acc_id")
    private Account recipientAccount;

    @ManyToOne
    @JoinColumn(name = "sender_card_id")
    private Card senderCard;

    @ManyToOne
    @JoinColumn(name = "recipient_card_id")
    private Card recipientCard;

    @Column(name = "amount")
    private double amount;

    @Column(name = "tr_date")
    private Date transactionDate;

    @Column(name = "tr_descr")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tr_status", columnDefinition = "ENUM('Pending', 'Completed', 'Failed')")
    private TransactionStatus status;
    
    public enum TransactionStatus {
        Pending,
        Completed,
        Failed
    }

}