package com.transactions_page.transactions_art.controllers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.transactions_page.transactions_art.models.Notes;
import com.transactions_page.transactions_art.models.Account;

public interface NotesService extends JpaRepository<Notes, Integer> {
    Optional<Notes> findByAccount(Account account);
    void deleteByAccount(Account account);
}
