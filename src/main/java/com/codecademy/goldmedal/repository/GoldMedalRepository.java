package com.codecademy.goldmedal.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codecademy.goldmedal.model.GoldMedal;

public interface GoldMedalRepository extends JpaRepository<GoldMedal, Long> {

    List<GoldMedal> findByCountry(String countryName, Sort by);
    
    List<GoldMedal> findBySeason(String season, Sort by);
    
    Integer countByCountry(String country);

    Integer countDistinctEventBySeason(String season);

    Integer countBySeason(String string);

    Integer countByGender(String string);
    
}
