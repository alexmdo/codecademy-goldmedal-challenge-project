package com.codecademy.goldmedal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codecademy.goldmedal.model.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByName(String countryName);
    
}
