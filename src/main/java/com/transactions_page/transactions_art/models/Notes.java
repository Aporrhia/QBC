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
@Table(name = "Notes")
public class Notes {

    @Id
    @Column(name = "note_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "description")
    public String description;
}