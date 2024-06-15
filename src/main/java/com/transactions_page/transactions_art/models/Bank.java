package com.transactions_page.transactions_art.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Bank")
public class Bank {

    @Id
    @Column(name = "bank_id")
    private int bankId;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_num")
    private String bankNum;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}