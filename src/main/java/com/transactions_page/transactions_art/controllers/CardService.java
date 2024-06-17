package com.transactions_page.transactions_art.controllers;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.transactions_page.transactions_art.models.Card;

public interface CardService extends JpaRepository<Card, Integer> {
    List<Card> findByAccountAccId(int account);
    List<Card> findByAccountAccIdAndStatus(int account, Card.CardStatus status);
    Card findByNumber(String number);
}