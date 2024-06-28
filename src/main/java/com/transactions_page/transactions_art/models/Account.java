package com.transactions_page.transactions_art.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Entity
@Data
@Table(name = "Account")
public class Account {

    @Id
    @Column(name = "acc_id")
    private int accId;

    @Column(name = "acc_number")
    private long accNum;
    
    @Column(name = "acc_fname")
    private String accFname;
    
    @Column(name = "acc_lname")
    private String accLname;
    
    @Column(name = "acc_email")
    private String accEmail;
    
    @Column(name = "acc_phone")
    private String accPhone;
    
    @Column(name = "accDOB")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date accDOB;

    @Column(name = "acc_balance")
    private double accBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('active', 'inactive')")
    private AccountStatus status;
    
    @Column(name = "acc_iban")
    private String accIban;
    
    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;
    
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
    
    @Column(name = "acc_creation_date")
    private Date accCreationDate;
    
    @Column(name = "acc_updated_date")
    private Date accUpdatedDate;

    @Column(name = "acc_password")
    private String accPassword;

    public void setBalance(double balance) {
        this.accBalance = balance;
    }

    public enum AccountStatus {
        active,
        inactive
    }
}