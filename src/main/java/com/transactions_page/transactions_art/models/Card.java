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
@Table(name = "Card")
public class Card {

    @Id
    @Column(name = "card_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", columnDefinition = "ENUM('debit', 'credit')")
    private CardType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_branch", columnDefinition = "ENUM('MasterCard', 'Visa')")
    private CardBranch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('active', 'inactive')")
    private CardStatus status;

    @Column(name = "card_number")
    private String number;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public enum CardType {
        debit,
        credit
    }
    
    public enum CardBranch {
        MasterCard,
        Visa
    }

    public enum CardStatus {
        active,
        inactive
    }

}