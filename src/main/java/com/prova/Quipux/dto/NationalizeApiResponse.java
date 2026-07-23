package com.prova.quipux.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NationalizeApiResponse(
        Long count,
        String name,
        List<CountryPrediction> country
){
    public record CountryPrediction(
            @JsonProperty("country_id")
            String countryId,
            Double probability
    ){}
}