package com.prova.quipux.service;

import com.prova.quipux.dto.NationalityResponse;
import com.prova.quipux.dto.NationalizeApiResponse;
import com.prova.quipux.entity.Person;
import com.prova.quipux.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.Comparator;
import java.util.IllformedLocaleException;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class NationalityService {

    private final PersonService personService;

    private final RestClient nationalizeRestClient;

    @Value("${nationalize.api-key:}")
    private String apiKey;

    public NationalityResponse findByPersonId(Long id) {

        Person person = personService.findEntityById(id);

        try {

            NationalizeApiResponse response =
                    nationalizeRestClient
                            .get()
                            .uri(uriBuilder -> {

                                uriBuilder
                                        .path("/")
                                        .queryParam(
                                                "name",
                                                person.getNome()
                                        );

                                if (StringUtils.hasText(apiKey)) {
                                    uriBuilder.queryParam(
                                            "apikey",
                                            apiKey
                                    );
                                }

                                return uriBuilder.build();
                            })
                            .retrieve()
                            .body(NationalizeApiResponse.class);

            if (
                    response == null
                            || response.country() == null
                            || response.country().isEmpty()
            ) {
                throw new ExternalServiceException(
                        "A API não retornou uma previsão de nacionalidade"
                );
            }

            NationalizeApiResponse.CountryPrediction prediction =
                    response
                            .country()
                            .stream()
                            .filter(country ->
                                    country.countryId() != null
                                            && country.probability() != null
                            )
                            .max(
                                    Comparator.comparingDouble(
                                            NationalizeApiResponse
                                                    .CountryPrediction
                                                    ::probability
                                    )
                            )
                            .orElseThrow(
                                    () -> new ExternalServiceException(
                                            "A API não retornou uma previsão válida"
                                    )
                            );

            String countryName = countryNameInPortuguese(
                    prediction.countryId()
            );

            return new NationalityResponse(
                    person.getId(),
                    person.getNome(),
                    prediction.countryId(),
                    countryName,
                    prediction.probability()
            );

        } catch (RestClientException exception) {

            throw new ExternalServiceException(
                    "Não foi possível consultar a API Nationalize",
                    exception
            );
        }
    }

    private String countryNameInPortuguese(
            String countryCode
    ) {

        try {

            Locale countryLocale = new Locale.Builder()
                    .setRegion(countryCode.toUpperCase())
                    .build();

            String countryName =
                    countryLocale.getDisplayCountry(
                            Locale.forLanguageTag("pt-BR")
                    );

            if (StringUtils.hasText(countryName)) {
                return countryName;
            }

            return countryCode;

        } catch (IllformedLocaleException exception) {

            return countryCode;
        }
    }
}