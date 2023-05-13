package com.codecademy.goldmedal.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.WordUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codecademy.goldmedal.model.CountriesResponse;
import com.codecademy.goldmedal.model.Country;
import com.codecademy.goldmedal.model.CountryDetailsResponse;
import com.codecademy.goldmedal.model.CountryMedalsListResponse;
import com.codecademy.goldmedal.model.CountrySummary;
import com.codecademy.goldmedal.model.GoldMedal;
import com.codecademy.goldmedal.repository.CountryRepository;
import com.codecademy.goldmedal.repository.GoldMedalRepository;

@RestController
@RequestMapping("/countries")
public class GoldMedalController {

    private final CountryRepository countryRepository;
    private final GoldMedalRepository goldMedalRepository;

    public GoldMedalController(CountryRepository countryRepository, GoldMedalRepository goldMedalRepository) {
        this.countryRepository = countryRepository;
        this.goldMedalRepository = goldMedalRepository;
    }

    @GetMapping
    public CountriesResponse getCountries(@RequestParam String sort_by, @RequestParam String ascending) {
        var ascendingOrder = ascending.toLowerCase().equals("y");
        return new CountriesResponse(getCountrySummaries(sort_by.toLowerCase(), ascendingOrder));
    }

    @GetMapping("/{country}")
    public CountryDetailsResponse getCountryDetails(@PathVariable String country) {
        String countryName = WordUtils.capitalizeFully(country);
        return getCountryDetailsResponse(countryName);
    }

    @GetMapping("/{country}/medals")
    public CountryMedalsListResponse getCountryMedalsList(@PathVariable String country, @RequestParam String sort_by, @RequestParam String ascending) {
        String countryName = WordUtils.capitalizeFully(country);
        var ascendingOrder = ascending.toLowerCase().equals("y");
        return getCountryMedalsListResponse(countryName, sort_by.toLowerCase(), ascendingOrder);
    }

    private CountryMedalsListResponse getCountryMedalsListResponse(String countryName, String sortBy, boolean ascendingOrder) {
        List<GoldMedal> medalsList;
        switch (sortBy) {
            case "year":
                medalsList = goldMedalRepository.findByCountry(countryName, Sort.by(ascendingOrder ? Direction.ASC : Direction.DESC, "year"));
                break;
            case "season":
                medalsList = goldMedalRepository.findByCountry(countryName, Sort.by(ascendingOrder ? Direction.ASC : Direction.DESC, "season"));
                break;
            case "city":
                medalsList = goldMedalRepository.findByCountry(countryName, Sort.by(ascendingOrder ? Direction.ASC : Direction.DESC, "city"));
                break;
            case "name":
                medalsList = goldMedalRepository.findByCountry(countryName, Sort.by(ascendingOrder ? Direction.ASC : Direction.DESC, "name"));
                break;
            case "event":
                medalsList = goldMedalRepository.findByCountry(countryName, Sort.by(ascendingOrder ? Direction.ASC : Direction.DESC, "event"));
                break;
            default:
                medalsList = new ArrayList<>();
                break;
        }

        return new CountryMedalsListResponse(medalsList);
    }

    private CountryDetailsResponse getCountryDetailsResponse(String countryName) {
        var countryOptional = countryRepository.findByName(countryName); // TODO: get the country; this repository method should return a java.util.Optional
        if (countryOptional.isEmpty()) {
            return new CountryDetailsResponse(countryName);
        }

        var country = countryOptional.get();
        var goldMedalCount = goldMedalRepository.countByCountry(country.getName());

        var summerWins = goldMedalRepository.findBySeason("Summer", Sort.by(Direction.ASC, "year")); // TODO: get the collection of wins at the Summer Olympics, sorted by year in ascending order
        var numberSummerWins = summerWins.size() > 0 ? summerWins.size() : null;
        var totalSummerEvents = goldMedalRepository.countDistinctEventBySeason("Summer"); // TODO: get the total number of events at the Summer Olympics
        var percentageTotalSummerWins = totalSummerEvents != 0 && numberSummerWins != null ? (float) summerWins.size() / totalSummerEvents : null;
        var yearFirstSummerWin = summerWins.size() > 0 ? summerWins.get(0).getYear() : null;

        var winterWins = goldMedalRepository.findBySeason("Winter", Sort.by(Direction.ASC, "year")); // TODO: get the collection of wins at the Winter Olympics
        var numberWinterWins = winterWins.size() > 0 ? winterWins.size() : null;
        var totalWinterEvents = goldMedalRepository.countBySeason("Winter"); // TODO: get the total number of events at the Winter Olympics, sorted by year in ascending order
        var percentageTotalWinterWins = totalWinterEvents != 0 && numberWinterWins != null ? (float) winterWins.size() / totalWinterEvents : null;
        var yearFirstWinterWin = winterWins.size() > 0 ? winterWins.get(0).getYear() : null;

        var numberEventsWonByFemaleAthletes = goldMedalRepository.countByGender("Women"); // TODO: get the number of wins by female athletes
        var numberEventsWonByMaleAthletes = goldMedalRepository.countByGender("Men"); // TODO: get the number of wins by male athletes

        return new CountryDetailsResponse(
                countryName,
                country.getGdp(),
                country.getPopulation(),
                goldMedalCount,
                numberSummerWins,
                percentageTotalSummerWins,
                yearFirstSummerWin,
                numberWinterWins,
                percentageTotalWinterWins,
                yearFirstWinterWin,
                numberEventsWonByFemaleAthletes,
                numberEventsWonByMaleAthletes);
    }

    private List<CountrySummary> getCountrySummaries(String sortBy, boolean ascendingOrder) {
        List<Country> countries;
        switch (sortBy) {
            case "name":
                countries = countryRepository.findAll(Sort.by(ascendingOrder ? Direction.ASC : Direction.DESC, "name")); // TODO: list of countries sorted by name in the given order
                break;
            case "gdp":
                countries = countryRepository.findAll(Sort.by(ascendingOrder ? Direction.ASC : Direction.DESC, "gdp")); // TODO: list of countries sorted by gdp in the given order
                break;
            case "population":
                countries = countryRepository.findAll(Sort.by(ascendingOrder ? Direction.ASC : Direction.DESC, "population")); // TODO: list of countries sorted by population in the given order
                break;
            case "medals":
            default:
                countries = countries = countryRepository.findAll(Sort.by(ascendingOrder ? Direction.ASC : Direction.DESC, "medals")); // TODO: list of countries in any order you choose; for sorting by medal count, additional logic below will handle that
                break;
        }

        var countrySummaries = getCountrySummariesWithMedalCount(countries);

        if (sortBy.equalsIgnoreCase("medals")) {
            countrySummaries = sortByMedalCount(countrySummaries, ascendingOrder);
        }

        return countrySummaries;
    }

    private List<CountrySummary> sortByMedalCount(List<CountrySummary> countrySummaries, boolean ascendingOrder) {
        return countrySummaries.stream()
                .sorted((t1, t2) -> ascendingOrder ?
                        t1.getMedals() - t2.getMedals() :
                        t2.getMedals() - t1.getMedals())
                .collect(Collectors.toList());
    }

    private List<CountrySummary> getCountrySummariesWithMedalCount(List<Country> countries) {
        List<CountrySummary> countrySummaries = new ArrayList<>();
        for (var country : countries) {
            var goldMedalCount = goldMedalRepository.countByCountry(country.getName()); // TODO: get count of medals for the given country
            countrySummaries.add(new CountrySummary(country, goldMedalCount));
        }
        return countrySummaries;
    }
}
