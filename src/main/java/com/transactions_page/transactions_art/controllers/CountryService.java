package com.transactions_page.transactions_art.controllers;

import org.springframework.data.jpa.repository.JpaRepository;
import com.transactions_page.transactions_art.models.Country;

public interface CountryService extends JpaRepository<Country, Integer> {
    Country findById (int countryId);
}